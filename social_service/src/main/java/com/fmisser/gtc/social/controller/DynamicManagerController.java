package com.fmisser.gtc.social.controller;

import com.fmisser.gtc.base.dto.social.DynamicDto;
import com.fmisser.gtc.base.response.ApiResp;
import com.fmisser.gtc.social.service.DynamicService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Api(description = "动态管理API")
@RestController
@RequestMapping("/dyamic-manager")
@Validated
public class DynamicManagerController {

    @Autowired
    private DynamicService dynamicService;

    @ApiOperation(value = "获取动态列表")
    @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "String", paramType = "header")
    @GetMapping("/list")
    @PreAuthorize("hasAnyRole('MANAGER')")
    ApiResp<List<DynamicDto>> getDynamicList(@RequestParam(value = "digitId", required = false) String digitId,
                                             @RequestParam(value = "nick", required = false) String nick,
                                             @RequestParam(value = "content", required = false) String content,
                                             @RequestParam(value = "startTime", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date startTime,
                                             @RequestParam(value = "endTime", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date endTime,
                                             @RequestParam(value = "pageIndex", required = false, defaultValue = "1") int pageIndex,
                                             @RequestParam(value = "pageSize", required = false, defaultValue = "30") int pageSize) {
        Pair<List<DynamicDto>, Map<String, Object>> dynamicList = dynamicService
                .managerListDynamic(digitId, nick, content, startTime, endTime, pageIndex, pageSize);
        return ApiResp.succeed(dynamicList.getFirst(), dynamicList.getSecond());
    }

    @ApiOperation(value = "获取守护动态审核")
    @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "String", paramType = "header")
    @GetMapping("/guard-list")
    @PreAuthorize("hasAnyRole('MANAGER')")
    ApiResp<List<DynamicDto>> getGuardDynamicList(@RequestParam(value = "digitId", required = false) String digitId,
                                             @RequestParam(value = "nick", required = false) String nick,
                                             @RequestParam(value = "content", required = false) String content,
                                             @RequestParam(value = "startTime", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date startTime,
                                             @RequestParam(value = "endTime", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date endTime,
                                             @RequestParam(value = "pageIndex", required = false, defaultValue = "1") int pageIndex,
                                             @RequestParam(value = "pageSize", required = false, defaultValue = "30") int pageSize) {
        Pair<List<DynamicDto>, Map<String, Object>> dynamicList = dynamicService
                .managerListDynamic(digitId, nick, content, startTime, endTime, pageIndex, pageSize);
        return ApiResp.succeed(dynamicList.getFirst(), dynamicList.getSecond());
    }

    @ApiOperation(value = "删除动态")
    @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "String", paramType = "header")
    @PostMapping("/del")
    @PreAuthorize("hasAnyRole('MANAGER')")
    ApiResp<Integer> delDynamic(@RequestParam("dynamicId") Long dynamicId) {
        return ApiResp.succeed(dynamicService.deleteDynamic(dynamicId));
    }

    @ApiOperation(value = "动态审核")
    @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "String", paramType = "header")
    @GetMapping("/audit")
    @PreAuthorize("hasAnyRole('MANAGER')")
    ApiResp<Integer> auditDynamic(@RequestParam("dynamicId") Long dynamicId,
                                  @RequestParam("pass") Integer pass,
                                  @RequestParam(value = "message", required = false, defaultValue = "") String message) {
        return ApiResp.succeed(dynamicService.auditDynamic(dynamicId, pass, message));
    }
}
