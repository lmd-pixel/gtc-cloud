package com.fmisser.gtc.social.controller;

import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.base.response.ApiResp;
import com.fmisser.gtc.social.domain.User;
import com.fmisser.gtc.social.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.hibernate.validator.constraints.Range;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.ws.rs.DELETE;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Api(description = "用户API")
@RestController
@RequestMapping("/user")
@Validated
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @ApiOperation(value = "创建用户")
    @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "String", paramType = "header")
    @PostMapping(value = "/create")
    ApiResp<User> create(@RequestParam("phone") String phone,
                         @RequestParam("gender") @Range(min = 0, max = 1) int gender) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getPrincipal().toString();
        if (!username.equals(phone)) {
            // return error
            throw new ApiException(-1, "非法操作，认证用户无法创建其他用户资料！");
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
        User userDo = userService.getUserByUsername(username);
        //
        // TODO: 2020/11/10 check params
        User user = userService.updateProfile(userDo, nick, birth, city, profession,
                intro, labels, callPrice, videoPrice, request.getFileMap());

        return ApiResp.succeed(user);
    }

    @ApiOperation(value = "更新用户照片")
    @ApiImplicitParam(name = "Authorization", required = true, dataType = "String", paramType = "header")
    @PostMapping(value = "/update-photos")
    ApiResp<User> uploadPhotos(MultipartHttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getPrincipal().toString();
        User userDo = userService.getUserByUsername(username);
        //
        // TODO: 2020/11/10 check params
        User user = userService.updatePhotos(userDo, request.getFileMap());

        return ApiResp.succeed(user);
    }

    @ApiOperation(value = "更新用户视频")
    @ApiImplicitParam(name = "Authorization", required = true, dataType = "String", paramType = "header")
    @PostMapping(value = "/update-video")
    ApiResp<User> uploadVideo(MultipartHttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getPrincipal().toString();
        User userDo = userService.getUserByUsername(username);
        //
        // TODO: 2020/11/10 check params
        User user = userService.updateVideo(userDo, request.getFileMap());

        return ApiResp.succeed(user);
    }

    @ApiOperation(value = "获取用户信息")
    @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "String", paramType = "header")
    @GetMapping(value = "/profile")
    ApiResp<User> getProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getPrincipal().toString();
        User userDo = userService.getUserByUsername(username);
        User user = userService.profile(userDo);
        return ApiResp.succeed(user);
    }

    @ApiOperation(value = "退出账号")
    @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "String", paramType = "header")
    @PostMapping(value = "/logout")
    ApiResp<Integer> logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getPrincipal().toString();
        User userDo = userService.getUserByUsername(username);
        int ret = userService.logout(userDo);
        return ApiResp.succeed(ret);
    }
}
