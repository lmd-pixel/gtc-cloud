package com.fmisser.gtc.social.controller;

import com.fmisser.gtc.base.response.ApiResp;
import com.fmisser.gtc.base.response.ApiRespHelper;
import com.fmisser.gtc.social.domain.Dynamic;
import com.fmisser.gtc.social.domain.DynamicHeart;
import com.fmisser.gtc.social.service.DynamicService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.util.List;

@Api("动态相关接口")
@RestController
@RequestMapping("/dynamic")
@Validated
public class DynamicController {
    @Autowired
    private DynamicService dynamicService;

    @Autowired
    private ApiRespHelper apiRespHelper;

    @ApiOperation(value = "创建动态")
    @ApiImplicitParam(name = "Authorization", required = true, dataType = "String", paramType = "header")
    @PostMapping(value = "/create")
    ApiResp<Dynamic> createDynamic(MultipartHttpServletRequest request,
                                   @RequestParam("userId") Long userId,
                                   @RequestParam("type") int type,
                                   @RequestParam("content") String content) {
        Dynamic dynamic = dynamicService.create(userId, type, content, request.getFileMap());
        return ApiResp.succeed(dynamic);
    }

//    @ApiOperation(value = "新评论或者点赞")
//    @ApiImplicitParam(name = "Authorization", required = true, dataType = "String", paramType = "header")
//    @PostMapping(value = "/interact/new")
//    ApiResp<Interact> newInteract(@RequestParam("dynamicId") Long dynamicId,
//                                  @RequestParam("userId") Long userId,
//                                  @RequestParam("type") int type,
//                                  @RequestParam(value = "content", required = false) String content) {
//        Interact interact = dynamicService.newInteract(dynamicId, userId, type, content);
//        return ApiResp.succeed(interact);
//    }
//
//    @ApiOperation(value = "取消点赞或者评论")
//    @ApiImplicitParam(name = "Authorization", required = true, dataType = "String", paramType = "header")
//    @PostMapping(value = "/interact/cancel")
//    ApiResp<Interact> cancelInteract(@RequestParam("interactId") Long interactId,
//                                  @RequestParam("userId") Long userId) {
//        Interact interact = dynamicService.cancelInteract(interactId, userId);
//        return ApiResp.succeed(interact);
//    }

    @ApiOperation(value = "点赞或者取消")
    @ApiImplicitParam(name = "Authorization", required = true, dataType = "String", paramType = "header")
    @PostMapping(value = "/heart")
    ApiResp<Integer> heart(@RequestParam("dynamicId") Long dynamicId,
                                @RequestParam("userId") Long userId,
                                @RequestParam("nickname") String nickname,
                                @RequestParam("isCancel") int isCancel) {
        int ret = dynamicService.dynamicHeart(dynamicId, userId, nickname, isCancel);
        return ApiResp.succeed(ret);
    }

    @ApiOperation(value = "动态列表")
    @ApiImplicitParam(name = "Authorization", required = true, dataType = "String", paramType = "header")
    @PostMapping(value = "/list")
    ApiResp<List<Dynamic>> getDynamicList(@RequestParam("userId") Long userId,
                                          @RequestParam("selfUserId") Long selfUserId,
                                          @RequestParam("pageIndex") int pageIndex,
                                          @RequestParam("pageSize") int pageSize) {
        List<Dynamic> dynamicList = dynamicService.getUserDynamicList(userId, selfUserId, pageIndex, pageSize);
        return ApiResp.succeed(dynamicList);
    }

}
