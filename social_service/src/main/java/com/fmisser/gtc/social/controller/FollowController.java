package com.fmisser.gtc.social.controller;

import com.fmisser.gtc.base.dto.social.FollowDto;
import com.fmisser.gtc.base.response.ApiResp;
import com.fmisser.gtc.social.mq.FollowNoticeBinding;
import com.fmisser.gtc.social.service.FollowService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(description = "关注API")
@RestController
@RequestMapping("/follow")
@Validated
public class FollowController {
    private final FollowService followService;
    private final FollowNoticeBinding followNoticeBinding;

    public FollowController(FollowService followService,
                            FollowNoticeBinding followNoticeBinding) {
        this.followService = followService;
        this.followNoticeBinding = followNoticeBinding;
    }

    @ApiOperation(value = "获取用户关注的人")
    @ApiImplicitParam(name = "Authorization", required = true, dataType = "String", paramType = "header")
    @GetMapping(value = "/from-list")
    ApiResp<List<FollowDto>> getFollowFromList(@RequestParam("userId") Long userId) {
        return followService.getFollowsFrom(userId);
    }

    @ApiOperation(value = "关注或者取消关注某人")
    @ApiImplicitParam(name = "Authorization", required = true, dataType = "String", paramType = "header")
    @PostMapping(value = "/op")
    ApiResp<FollowDto> followOp(@RequestParam("userIdFrom") Long userIdFrom,
                                @RequestParam("userIdTo") Long userIdTo,
                                @RequestParam("follow") int follow) {
        return followService.follow(userIdFrom, userIdTo, follow == 1);
    }

    @ApiOperation(value = "获取关注某个用户的人")
    @ApiImplicitParam(name = "Authorization", required = true, dataType = "String", paramType = "header")
    @GetMapping(value = "/to-list")
//    @PreAuthorize("hasAnyRole('SERVER')")
//    @PreAuthorize("hasRole('ADMIN')")
//    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
//    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
//    @PreAuthorize("#oauth2.hasAnyScope('server')")
//    @PreAuthorize("#principal.username.equals('server_openfeign')")
    ApiResp<List<FollowDto>> getFollowToList(@RequestParam("userId") Long userId) {
        return followService.getFollowsTo(userId);
    }

    @GetMapping(value = "/notice")
    boolean notice(@RequestParam("youngId") Long youngId) {
        Message<Long> message = MessageBuilder.withPayload(youngId).build();
        return followNoticeBinding.followNoticeChannel().send(message);
    }
}
