package com.fmisser.gtc.social.controller;

import com.fmisser.gtc.base.response.ApiResp;
import com.fmisser.gtc.social.domain.User;
import com.fmisser.gtc.social.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(description = "通用API")
@RestController
@RequestMapping("/comm")
@Validated
public class CommonController {
    private final UserService userService;

    public CommonController(UserService userService) {
        this.userService = userService;
    }

    @ApiOperation(value = "获取主播列表")
    @PostMapping(value = "/list-anchor")
    ApiResp<List<User>> getAnchorList(@RequestParam(value = "type") int type,
                                      @RequestParam(value = "pageIndex", required = false, defaultValue = "0") int pageIndex,
                                      @RequestParam(value = "pageSize", required = false, defaultValue = "30") int pageSize) {
        List<User> userList = userService.getAnchorList(type, pageIndex, pageSize);
        return ApiResp.succeed(userList);
    }
}
