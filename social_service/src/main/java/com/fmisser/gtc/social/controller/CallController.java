package com.fmisser.gtc.social.controller;

import com.fmisser.gtc.base.dto.social.UserCallDto;
import com.fmisser.gtc.base.response.ApiResp;
import com.fmisser.gtc.social.domain.User;
import com.fmisser.gtc.social.service.CallService;
import com.fmisser.gtc.social.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(description = "通话API")
@RestController
@RequestMapping("/call")
@Validated
public class CallController {
    @Autowired
    private CallService callService;

    @Autowired
    private UserService userService;

    @ApiOperation(value = "通话列表")
    @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "String", paramType = "header")
    @GetMapping("/list")
    ApiResp<List<UserCallDto>> getCallList(@RequestParam("pageIndex") int pageIndex,
                                           @RequestParam("pageSize") int pageSize) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getPrincipal().toString();
        User userDo = userService.getUserByUsername(username);

        List<UserCallDto> userCallDtoList = callService.getCallList(userDo, pageIndex, pageSize);
        return ApiResp.succeed(userCallDtoList);
    }
}
