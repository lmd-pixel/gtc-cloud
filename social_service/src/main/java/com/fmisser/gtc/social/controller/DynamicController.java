package com.fmisser.gtc.social.controller;

import com.fmisser.gtc.base.dto.social.DynamicCommentDto;
import com.fmisser.gtc.base.dto.social.DynamicDto;
import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.base.response.ApiResp;
import com.fmisser.gtc.social.domain.Dynamic;
import com.fmisser.gtc.social.domain.User;
import com.fmisser.gtc.social.service.DynamicService;
import com.fmisser.gtc.social.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Api(description = "动态API")
@RestController
@RequestMapping("/dynamic")
@Validated
public class DynamicController {
    private final DynamicService dynamicService;
    private final UserService userService;

    public DynamicController(DynamicService dynamicService,
                             UserService userService) {
        this.dynamicService = dynamicService;
        this.userService = userService;
    }

    @ApiOperation(value = "创建动态")
    @ApiImplicitParam(name = "Authorization", required = true, dataType = "String", paramType = "header")
    @PostMapping(value = "/create")
    @PreAuthorize("hasAnyRole('USER')")
    ApiResp<DynamicDto> createDynamic(MultipartHttpServletRequest request,
                                      @RequestHeader(value = "version", required = false, defaultValue = "v1") String version,
                                      @RequestParam("type") int type,
                                      @RequestParam("content") String content,
                                      @RequestParam(value = "city", required = false) String city,
                                      @RequestParam(value = "longitude", required = false) BigDecimal longitude,
                                      @RequestParam(value = "latitude", required = false) BigDecimal latitude) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getPrincipal().toString();
        User userDo = userService.getUserByUsername(username);

        DynamicDto dynamicDto = dynamicService
                .create(userDo, type, content, city, longitude, latitude, request.getFileMap());

        // 兼容以前的版本
        DynamicDto compatDynamicDto = dynamicService.compat(Collections.singletonList(dynamicDto), version).get(0);

        return ApiResp.succeed(compatDynamicDto);
    }

    @ApiOperation(value = "删除动态")
    @ApiImplicitParam(name = "Authorization", required = true, dataType = "String", paramType = "header")
    @PostMapping(value = "/del")
    @PreAuthorize("hasAnyRole('USER')")
    ApiResp<Integer> createDynamic(@RequestParam("dynamicId") Long dynamicId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getPrincipal().toString();
        User userDo = userService.getUserByUsername(username);

        int ret = dynamicService.delete(userDo, dynamicId);
        return ApiResp.succeed(ret);
    }

    @ApiOperation(value = "点赞或者取消")
    @ApiImplicitParam(name = "Authorization", required = true, dataType = "String", paramType = "header")
    @PostMapping(value = "/heart")
    @PreAuthorize("hasAnyRole('USER')")
    ApiResp<Integer> heart(@RequestParam("dynamicId") Long dynamicId,
                           @RequestParam("isCancel") int isCancel) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getPrincipal().toString();
        User userDo = userService.getUserByUsername(username);

        int ret = dynamicService.dynamicHeart(dynamicId, userDo, isCancel);
        return ApiResp.succeed(ret);
    }

    @ApiOperation(value = "添加评论")
    @ApiImplicitParam(name = "Authorization", required = true, dataType = "String", paramType = "header")
    @PostMapping(value = "/add-comment")
    @PreAuthorize("hasAnyRole('USER')")
    ApiResp<Integer> addComment(@RequestParam("dynamicId") Long dynamicId,
                                @RequestParam(value = "commentIdTo", required = false) Long commentIdTo,
                                @RequestParam(value = "toUserId", required = false) String digitIdTo,
                                @RequestParam("content") String content) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getPrincipal().toString();
        User userDo = userService.getUserByUsername(username);

        User userDoTo = null;
        if (Objects.nonNull(digitIdTo)) {
            userDoTo = userService.getUserByDigitId(digitIdTo);
        }

        int ret = dynamicService.newDynamicComment(dynamicId, commentIdTo, userDo, userDoTo, content);
        return ApiResp.succeed(ret);
    }

    @ApiOperation(value = "删除评论")
    @ApiImplicitParam(name = "Authorization", required = true, dataType = "String", paramType = "header")
    @PostMapping(value = "/del-comment")
    @PreAuthorize("hasAnyRole('USER')")
    ApiResp<Integer> delComment(@RequestParam("commentId") Long commentId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getPrincipal().toString();
        User userDo = userService.getUserByUsername(username);

        int ret = dynamicService.delDynamicComment(commentId, userDo);
        return ApiResp.succeed(ret);
    }

    @ApiOperation(value = "获取评论列表")
    @ApiImplicitParam(name = "Authorization", required = true, dataType = "String", paramType = "header")
    @PostMapping(value = "/list-comments")
//    @PreAuthorize("hasAnyRole('USER')")
    ApiResp<List<DynamicCommentDto>> listComments(@RequestParam("commentId") Long commentId,
                                                  @RequestParam("pageIndex") int pageIndex,
                                                  @RequestParam("pageSize") int pageSize) {
        User selfUser = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.isAuthenticated()) {
            String username = authentication.getPrincipal().toString();
            // 未授权默认登录的账户名字未 anonymousUser
            if (!username.equals("anonymousUser")) {
                selfUser = userService.getUserByUsername(username);
            }
        }

        // TODO: 2021/1/26 这里 commentId 应该是 dynamicId
        List<DynamicCommentDto> dynamicCommentDtos = dynamicService.getDynamicCommentList(commentId, selfUser, pageIndex, pageSize);
        return ApiResp.succeed(dynamicCommentDtos);
    }

    @ApiOperation(value = "获取某个用户的动态列表")
    @ApiImplicitParam(name = "Authorization", required = false, dataType = "String", paramType = "header")
    @PostMapping(value = "/list-by-user")
    ApiResp<List<DynamicDto>> getDynamicList(
            @RequestHeader(value = "version", required = false, defaultValue = "v1") String version,
            @RequestParam("digitId") String digitId,
            @RequestParam("pageIndex") int pageIndex,
            @RequestParam("pageSize") int pageSize) {
        User user = userService.getUserByDigitId(digitId);
        User selfUser = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.isAuthenticated()) {
            String username = authentication.getPrincipal().toString();
            // 未授权默认登录的账户名字未 anonymousUser
            if (!username.equals("anonymousUser")) {
                selfUser = userService.getUserByUsername(username);
            }
        }

        List<DynamicDto> dynamicList = dynamicService.getUserDynamicList(user, selfUser, pageIndex, pageSize, version);

        // 兼容以前代码
        List<DynamicDto> compatDynamicList = dynamicService.compat(dynamicList, version);

        return ApiResp.succeed(compatDynamicList);
    }

    @ApiOperation(value = "关注的人的动态列表")
    @ApiImplicitParam(name = "Authorization", required = true, dataType = "String", paramType = "header")
    @PostMapping(value = "/list-by-follow")
    @PreAuthorize("hasAnyRole('USER')")
    ApiResp<List<DynamicDto>> getFollowDynamicList(
            @RequestHeader(value = "version", required = false, defaultValue = "v1") String version,
            @RequestParam("pageIndex") int pageIndex,
            @RequestParam("pageSize") int pageSize) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getPrincipal().toString();
        User userDo = userService.getUserByUsername(username);

        List<DynamicDto> dynamicList = dynamicService.getFollowLatestDynamicList(userDo, pageIndex, pageSize, version);

        // 兼容以前代码
        List<DynamicDto> compatDynamicList = dynamicService.compat(dynamicList, version);

        return ApiResp.succeed(compatDynamicList);
    }

    @ApiOperation(value = "获取最新的动态列表")
    @ApiImplicitParam(name = "Authorization", required = false, dataType = "String", paramType = "header")
    @PostMapping(value = "/list-latest")
    ApiResp<List<DynamicDto>> getLatestDynamicList(
            @RequestHeader(value = "version", required = false, defaultValue = "v1") String version,
            @RequestParam("pageIndex") int pageIndex,
            @RequestParam("pageSize") int pageSize) throws ParseException {
        User selfUser = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.isAuthenticated()) {
            String username = authentication.getPrincipal().toString();
            // 未授权默认登录的账户名字未 anonymousUser
            if (!username.equals("anonymousUser")) {
                selfUser = userService.getUserByUsername(username);
            }
        }

        List<DynamicDto> dynamicList = dynamicService.getLatestDynamicList(selfUser, pageIndex, pageSize, version);

        // 兼容以前代码
        List<DynamicDto> compatDynamicList = dynamicService.compat(dynamicList, version);

        return ApiResp.succeed(compatDynamicList);
    }

}
