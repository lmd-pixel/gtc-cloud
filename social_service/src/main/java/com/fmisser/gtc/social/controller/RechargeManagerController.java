package com.fmisser.gtc.social.controller;

import com.fmisser.gtc.base.dto.social.RechargeDto;
import com.fmisser.gtc.base.response.ApiResp;
import com.fmisser.gtc.social.service.RechargeManagerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.hibernate.validator.constraints.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Api(description = "充值管理")
@RestController
@RequestMapping("/recharge-manager")
@Validated
public class RechargeManagerController {

    private final RechargeManagerService rechargeManagerService;

    public RechargeManagerController(RechargeManagerService rechargeManagerService) {
        this.rechargeManagerService = rechargeManagerService;
    }

    @ApiOperation(value = "获取充值记录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "String", paramType = "header"),
            @ApiImplicitParam(name = "digitId", value = "用户ID", paramType = "query", required = false),
            @ApiImplicitParam(name = "nick", value = "用户昵称", paramType = "query", required = false),
            @ApiImplicitParam(name = "status", value = "充值状态： 0: 未完成 1:已完成 2: 全部（全部）", paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "startTime", value = "起始时间", paramType = "query", dataType = "date", required = false),
            @ApiImplicitParam(name = "endTime", value = "结束时间", paramType = "query", dataType = "date", required = false),
            @ApiImplicitParam(name = "pageIndex", value = "展示第几页", paramType = "query", defaultValue = "1", dataType = "Integer"),
            @ApiImplicitParam(name = "pageSize", value = "每页数据条数", paramType = "query", defaultValue = "30", dataType = "Integer")
    })
    @PreAuthorize("hasAnyRole('MANAGER')")
    @RequestMapping(value = "/list-recharge", method = RequestMethod.GET)
    public ApiResp<List<RechargeDto>> getRechargeList(
            @RequestParam(value = "digitId", required = false) String digitId,
            @RequestParam(value = "nick", required = false) String nick,
            @RequestParam(value = "status", required = false, defaultValue = "2") @Range(min = 0, max = 2) Integer status,
            @RequestParam(value = "startTime", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date startTime,
            @RequestParam(value = "endTime", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date endTime,
            @RequestParam(value = "pageIndex", required = false, defaultValue = "1") int pageIndex,
            @RequestParam(value = "pageSize", required = false, defaultValue = "30") int pageSize) {
        Pair<List<RechargeDto>, Map<String, Object>> rechargeDtoList =
                rechargeManagerService.getRechargeList(digitId, nick, status, startTime, endTime, pageIndex, pageSize);
        return ApiResp.succeed(rechargeDtoList.getFirst(), rechargeDtoList.getSecond());
    }
}
