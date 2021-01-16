package com.fmisser.gtc.social.controller;

import com.fmisser.gtc.base.dto.social.ConcernDto;
import com.fmisser.gtc.base.dto.social.FansDto;
import com.fmisser.gtc.base.dto.social.FollowDto;
import com.fmisser.gtc.base.response.ApiResp;
import com.fmisser.gtc.social.domain.User;
import com.fmisser.gtc.social.mq.FollowNoticeBinding;
import com.fmisser.gtc.social.service.FollowService;
import com.fmisser.gtc.social.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final UserService userService;

    public FollowController(FollowService followService,
                            FollowNoticeBinding followNoticeBinding,
                            UserService userService) {
        this.followService = followService;
        this.followNoticeBinding = followNoticeBinding;
        this.userService = userService;
    }

    @ApiOperation(value = "获取用户关注的人")
    @ApiImplicitParam(name = "Authorization", required = true, dataType = "String", paramType = "header")
    @GetMapping(value = "/from-list")
        // TODO: 2020/12/21 不要使用用户自己传的user id
    ApiResp<List<FollowDto>> getFollowFromList(@RequestParam("userId") Long userId) {
        return followService.getFollowsFrom(userId);
    }

    @ApiOperation(value = "关注或者取消关注某人")
    @ApiImplicitParam(name = "Authorization", required = true, dataType = "String", paramType = "header")
    @PostMapping(value = "/op")
    ApiResp<FollowDto> followOp(@RequestParam("digitId") String digitId,
                                @RequestParam("follow") int follow) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getPrincipal().toString();
        User userDo = userService.getUserByUsername(username);

        User userTo = userService.getUserByDigitId(digitId);

        return followService.follow(userDo, userTo, follow == 1);
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

    @ApiOperation(value = "获取关注列表")
    @ApiImplicitParam(name = "Authorization", required = true, dataType = "String", paramType = "header")
    @GetMapping(value = "/follow-list")
    ApiResp<List<ConcernDto>> getFollowList(@RequestParam("pageIndex") int pageIndex,
                                            @RequestParam("pageSize") int pageSize) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getPrincipal().toString();
        User userDo = userService.getUserByUsername(username);

        List<ConcernDto> concernDtoList = followService.getConcernList(userDo, pageIndex, pageSize);
        return ApiResp.succeed(concernDtoList);
    }

    @ApiOperation(value = "获取粉丝列表")
    @ApiImplicitParam(name = "Authorization", required = true, dataType = "String", paramType = "header")
    @GetMapping(value = "/fans-list")
    ApiResp<List<FansDto>> getFansList(@RequestParam("pageIndex") int pageIndex,
                                   @RequestParam("pageSize") int pageSize) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getPrincipal().toString();
        User userDo = userService.getUserByUsername(username);

        List<FansDto> fansDtoList = followService.getFansList(userDo, pageIndex, pageSize);
        return ApiResp.succeed(fansDtoList);
    }
}
