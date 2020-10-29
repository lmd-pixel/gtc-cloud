package com.fmisser.gtc.social.controller;

import com.fmisser.gtc.social.mq.FollowNoticeBinding;
import com.fmisser.gtc.social.service.FollowService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api("关注相关接口")
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

    @ApiOperation(value = "获取关注用户")
//    @ApiImplicitParam(name = "Authorization", required = true, dataType = "String", paramType = "header")
    @GetMapping(value = "/list")
    List<Long> getFollowList(@RequestParam("youngId") Long youngId) {
        return followService.getFollows(youngId);
    }

    @GetMapping(value = "/notice")
    boolean notice(@RequestParam("youngId") Long youngId) {
        Message<Long> message = MessageBuilder.withPayload(youngId).build();
        return followNoticeBinding.followNoticeChannel().send(message);
    }
}
