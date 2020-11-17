package com.fmisser.gtc.social.controller;

import com.fmisser.gtc.base.response.ApiResp;
import com.fmisser.gtc.social.domain.User;
import com.fmisser.gtc.social.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Api("用户相关接口")
@RestController
@RequestMapping("/user")
@Validated
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @ApiOperation(value = "创建用户")
    @ApiImplicitParam(name = "Authorization", required = true, dataType = "String", paramType = "header")
    @PostMapping(value = "/create")
    ApiResp<User> create(@RequestParam("phone") String phone,
                         @RequestParam("gender") int gender) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getPrincipal().toString();
        if (!username.equals(phone)) {
            // return error
        }

        if (gender != 0 && gender != 1) {
            // return error
        }

        return ApiResp.succeed(userService.create(phone, gender));
    }

    @ApiOperation(value = "更新用户信息")
    @ApiImplicitParam(name = "Authorization", required = true, dataType = "String", paramType = "header")
    @PostMapping(value = "/update-profile")
    ApiResp<User> uploadProfile(MultipartHttpServletRequest request,
                                @RequestParam(value = "nick", required = false) String nick,
                                @RequestParam(value = "birth", required = false) String birth,
                                @RequestParam(value = "city", required = false) String city,
                                @RequestParam(value = "profession", required = false) String profession,
                                @RequestParam(value = "intro", required = false) String intro,
                                @RequestParam(value = "labels", required = false) String labels,
                                @RequestParam(value = "callPrice", required = false) String callPrice,
                                @RequestParam(value = "videoPrice", required = false) String videoPrice) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getPrincipal().toString();
        // TODO: 2020/11/10 check params
        User user = userService.updateProfile(username, nick, birth, city, profession,
                intro, labels, callPrice, videoPrice, request.getFileMap());

        return ApiResp.succeed(user);
    }
}
