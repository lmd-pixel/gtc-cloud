package com.fmisser.gtc.social.controller;

import com.fmisser.gtc.social.service.FollowService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api("关注相关接口")
@RestController
@RequestMapping("/follow")
@Validated
public class FollowController {
    private final FollowService followService;

    public FollowController(FollowService followService) {
        this.followService = followService;
    }

    @ApiOperation(value = "获取关注用户")
//    @ApiImplicitParam(name = "Authorization", required = true, dataType = "String", paramType = "header")
    @GetMapping(value = "/list")
    List<Long> getFollowList(@RequestParam("youngId") Long youngId) {
        return followService.getFollows(youngId);
    }
}
