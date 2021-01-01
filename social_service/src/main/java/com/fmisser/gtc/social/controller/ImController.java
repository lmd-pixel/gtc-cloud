package com.fmisser.gtc.social.controller;

import com.fmisser.gtc.base.response.ApiResp;
import com.fmisser.gtc.social.domain.User;
import com.fmisser.gtc.social.service.ImService;
import com.fmisser.gtc.social.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Api(description = "聊天API")
@RestController
@RequestMapping("/im")
public class ImController {

    @Autowired
    private ImService imService;

    @Autowired
    private UserService userService;

    @GetMapping("/user-sig")
    @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "String", paramType = "header")
    public ApiResp<String> getUserSig() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getPrincipal().toString();
        User userDo = userService.getUserByUsername(username);

        String userSign = imService.genUserSign(userDo);

        return ApiResp.succeed(userSign);
    }

    @PostMapping("/send-msg")
    @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "String", paramType = "header")
    public ApiResp<Integer> sendMsg(@RequestParam("digitId") String digitId,
                                    @RequestParam("content") String content) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getPrincipal().toString();
        User userDo = userService.getUserByUsername(username);

        User toUser = userService.getUserByDigitId(digitId);

        return ApiResp.succeed(imService.sendToUser(userDo, toUser, content));
    }
}
