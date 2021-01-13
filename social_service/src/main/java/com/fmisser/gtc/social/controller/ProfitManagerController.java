package com.fmisser.gtc.social.controller;

import com.fmisser.gtc.base.dto.social.*;
import com.fmisser.gtc.base.response.ApiResp;
import com.fmisser.gtc.social.service.ProfitManagerService;
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

@Api(description = "收益管理")
@RestController
@RequestMapping("/profit-manager")
@Validated
public class ProfitManagerController {
    @Autowired
    private ProfitManagerService profitManagerService;

    @ApiOperation(value = "获取主播通话收益列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "String", paramType = "header"),
            @ApiImplicitParam(name = "digitId", value = "用户ID", paramType = "query", required = false),
            @ApiImplicitParam(name = "nick", value = "用户昵称", paramType = "query", required = false),
            @ApiImplicitParam(name = "type", value = "通话类型： 0: 语音通话， 1：视频通话",  paramType = "query", defaultValue = "0", dataType = "Integer"),
            @ApiImplicitParam(name = "startTime", value = "起始时间", paramType = "query", dataType = "date", required = false),
            @ApiImplicitParam(name = "endTime", value = "结束时间", paramType = "query", dataType = "date", required = false),
            @ApiImplicitParam(name = "pageIndex", value = "展示第几页", paramType = "query", defaultValue = "1", dataType = "Integer"),
            @ApiImplicitParam(name = "pageSize", value = "每页数据条数", paramType = "query", defaultValue = "30", dataType = "Integer")
    })
    @PreAuthorize("hasAnyRole('MANAGER')")
    @RequestMapping(value = "/list-anchor-call", method = RequestMethod.GET)
    ApiResp<List<AnchorCallBillDto>> getAnchorCallBillList(
            @RequestParam(value = "digitId", required = false) String digitId,
            @RequestParam(value = "nick", required = false) String nick,
            @RequestParam(value = "type") @Range(min = 0, max = 1) Integer type,
            @RequestParam(value = "startTime", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date startTime,
            @RequestParam(value = "endTime", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date endTime,
            @RequestParam(value = "pageIndex", required = false, defaultValue = "1") int pageIndex,
            @RequestParam(value = "pageSize", required = false, defaultValue = "30") int pageSize) {
        Pair<List<AnchorCallBillDto>, Map<String, Object>> anchorCallBillDtoList = profitManagerService
                .getAnchorCallProfitList(digitId, nick, startTime, endTime, type, pageIndex, pageSize);
        return ApiResp.succeed(anchorCallBillDtoList.getFirst(), anchorCallBillDtoList.getSecond());
    }

    @ApiOperation(value = "获取主播私信收益列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "String", paramType = "header"),
            @ApiImplicitParam(name = "digitId", value = "用户ID", paramType = "query", required = false),
            @ApiImplicitParam(name = "nick", value = "用户昵称", paramType = "query", required = false),
            @ApiImplicitParam(name = "startTime", value = "起始时间", paramType = "query", dataType = "date", required = false),
            @ApiImplicitParam(name = "endTime", value = "结束时间", paramType = "query", dataType = "date", required = false),
            @ApiImplicitParam(name = "pageIndex", value = "展示第几页", paramType = "query", defaultValue = "1", dataType = "Integer"),
            @ApiImplicitParam(name = "pageSize", value = "每页数据条数", paramType = "query", defaultValue = "30", dataType = "Integer")
    })
    @PreAuthorize("hasAnyRole('MANAGER')")
    @RequestMapping(value = "/list-anchor-msg", method = RequestMethod.GET)
    ApiResp<List<AnchorMessageBillDto>> getAnchorMsgBillList(
            @RequestParam(value = "digitId", required = false) String digitId,
            @RequestParam(value = "nick", required = false) String nick,
            @RequestParam(value = "startTime", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date startTime,
            @RequestParam(value = "endTime", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date endTime,
            @RequestParam(value = "pageIndex", required = false, defaultValue = "1") int pageIndex,
            @RequestParam(value = "pageSize", required = false, defaultValue = "30") int pageSize) {
        Pair<List<AnchorMessageBillDto>, Map<String, Object>> anchorMessageBillDtoList = profitManagerService
                .getAnchorMessageProfitList(digitId, nick, startTime, endTime, pageIndex, pageSize);
        return ApiResp.succeed(anchorMessageBillDtoList.getFirst(), anchorMessageBillDtoList.getSecond());
    }

    @ApiOperation(value = "获取主播礼物收益列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "String", paramType = "header"),
            @ApiImplicitParam(name = "digitId", value = "用户ID", paramType = "query", required = false),
            @ApiImplicitParam(name = "nick", value = "用户昵称", paramType = "query", required = false),
            @ApiImplicitParam(name = "startTime", value = "起始时间", paramType = "query", dataType = "date", required = false),
            @ApiImplicitParam(name = "endTime", value = "结束时间", paramType = "query", dataType = "date", required = false),
            @ApiImplicitParam(name = "pageIndex", value = "展示第几页", paramType = "query", defaultValue = "1", dataType = "Integer"),
            @ApiImplicitParam(name = "pageSize", value = "每页数据条数", paramType = "query", defaultValue = "30", dataType = "Integer")
    })
    @PreAuthorize("hasAnyRole('MANAGER')")
    @RequestMapping(value = "/list-anchor-gift", method = RequestMethod.GET)
    ApiResp<List<AnchorGiftBillDto>> getAnchorGiftBillList(
            @RequestParam(value = "digitId", required = false) String digitId,
            @RequestParam(value = "nick", required = false) String nick,
            @RequestParam(value = "startTime", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date startTime,
            @RequestParam(value = "endTime", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date endTime,
            @RequestParam(value = "pageIndex", required = false, defaultValue = "1") int pageIndex,
            @RequestParam(value = "pageSize", required = false, defaultValue = "30") int pageSize) {
        Pair<List<AnchorGiftBillDto>, Map<String, Object>> anchorGiftBillDtoList = profitManagerService
                .getAnchorGiftProfitList(digitId, nick, startTime, endTime, pageIndex, pageSize);
        return ApiResp.succeed(anchorGiftBillDtoList.getFirst(), anchorGiftBillDtoList.getSecond());
    }

    @ApiOperation(value = "获取用户通话消费列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "String", paramType = "header"),
            @ApiImplicitParam(name = "consumerDigitId", value = "用户ID", paramType = "query", required = false),
            @ApiImplicitParam(name = "consumerNick", value = "用户昵称", paramType = "query", required = false),
            @ApiImplicitParam(name = "anchorDigitId", value = "主播ID", paramType = "query", required = false),
            @ApiImplicitParam(name = "anchorNick", value = "主播昵称", paramType = "query", required = false),
            @ApiImplicitParam(name = "type", value = "通话类型： 0: 语音通话， 1：视频通话", paramType = "query", defaultValue = "0", dataType = "Integer"),
            @ApiImplicitParam(name = "startTime", value = "起始时间", paramType = "query", dataType = "date", required = false),
            @ApiImplicitParam(name = "endTime", value = "结束时间", paramType = "query", dataType = "date", required = false),
            @ApiImplicitParam(name = "pageIndex", value = "展示第几页", paramType = "query", defaultValue = "1", dataType = "Integer"),
            @ApiImplicitParam(name = "pageSize", value = "每页数据条数", paramType = "query", defaultValue = "30", dataType = "Integer")
    })
    @PreAuthorize("hasAnyRole('MANAGER')")
    @RequestMapping(value = "/list-consumer-call", method = RequestMethod.GET)
    ApiResp<List<ConsumerCallBillDto>> getAnchorCallBillList(
            @RequestParam(value = "consumerDigitId", required = false) String consumerDigitId,
            @RequestParam(value = "consumerNick", required = false) String consumerNick,
            @RequestParam(value = "anchorDigitId", required = false) String anchorDigitId,
            @RequestParam(value = "anchorNick", required = false) String anchorNick,
            @RequestParam(value = "type") @Range(min = 0, max = 1) Integer type,
            @RequestParam(value = "startTime", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date startTime,
            @RequestParam(value = "endTime", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date endTime,
            @RequestParam(value = "pageIndex", required = false, defaultValue = "1") int pageIndex,
            @RequestParam(value = "pageSize", required = false, defaultValue = "30") int pageSize) {
        Pair<List<ConsumerCallBillDto>, Map<String, Object>> consumerCallBillDtoList = profitManagerService
                .getConsumerCallBillList(consumerDigitId, consumerNick,
                        anchorDigitId, anchorNick, startTime, endTime, type, pageIndex, pageSize);
        return ApiResp.succeed(consumerCallBillDtoList.getFirst(), consumerCallBillDtoList.getSecond());
    }

    @ApiOperation(value = "获取用户私信消费列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "String", paramType = "header"),
            @ApiImplicitParam(name = "consumerDigitId", value = "用户ID", paramType = "query", required = false),
            @ApiImplicitParam(name = "consumerNick", value = "用户昵称", paramType = "query", required = false),
            @ApiImplicitParam(name = "anchorDigitId", value = "主播ID", paramType = "query", required = false),
            @ApiImplicitParam(name = "anchorNick", value = "主播昵称", paramType = "query", required = false),
            @ApiImplicitParam(name = "startTime", value = "起始时间", paramType = "query", dataType = "date", required = false),
            @ApiImplicitParam(name = "endTime", value = "结束时间", paramType = "query", dataType = "date", required = false),
            @ApiImplicitParam(name = "pageIndex", value = "展示第几页", paramType = "query", defaultValue = "1", dataType = "Integer"),
            @ApiImplicitParam(name = "pageSize", value = "每页数据条数", paramType = "query", defaultValue = "30", dataType = "Integer")
    })
    @PreAuthorize("hasAnyRole('MANAGER')")
    @RequestMapping(value = "/list-consumer-msg", method = RequestMethod.GET)
    ApiResp<List<ConsumerMessageBillDto>> getConsumerMsgBillList(
            @RequestParam(value = "consumerDigitId", required = false) String consumerDigitId,
            @RequestParam(value = "consumerNick", required = false) String consumerNick,
            @RequestParam(value = "anchorDigitId", required = false) String anchorDigitId,
            @RequestParam(value = "anchorNick", required = false) String anchorNick,
            @RequestParam(value = "startTime", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date startTime,
            @RequestParam(value = "endTime", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date endTime,
            @RequestParam(value = "pageIndex", required = false, defaultValue = "1") int pageIndex,
            @RequestParam(value = "pageSize", required = false, defaultValue = "30") int pageSize) {
        Pair<List<ConsumerMessageBillDto>, Map<String,Object>> consumerMessageBillDtoList = profitManagerService
                .getConsumerMsgBillList(consumerDigitId, consumerNick,
                        anchorDigitId, anchorNick,
                        startTime, endTime,
                        pageIndex, pageSize);
        return ApiResp.succeed(consumerMessageBillDtoList.getFirst(), consumerMessageBillDtoList.getSecond());
    }

    @ApiOperation(value = "获取用户礼物消费列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "String", paramType = "header"),
            @ApiImplicitParam(name = "consumerDigitId", value = "用户ID", paramType = "query", required = false),
            @ApiImplicitParam(name = "consumerNick", value = "用户昵称", paramType = "query", required = false),
            @ApiImplicitParam(name = "anchorDigitId", value = "主播ID", paramType = "query", required = false),
            @ApiImplicitParam(name = "anchorNick", value = "主播昵称", paramType = "query", required = false),
            @ApiImplicitParam(name = "startTime", value = "起始时间", paramType = "query", dataType = "date", required = false),
            @ApiImplicitParam(name = "endTime", value = "结束时间", paramType = "query", dataType = "date", required = false),
            @ApiImplicitParam(name = "pageIndex", value = "展示第几页", paramType = "query", defaultValue = "1", dataType = "Integer"),
            @ApiImplicitParam(name = "pageSize", value = "每页数据条数", paramType = "query", defaultValue = "30", dataType = "Integer")
    })
    @PreAuthorize("hasAnyRole('MANAGER')")
    @RequestMapping(value = "/list-consumer-gift", method = RequestMethod.GET)
    ApiResp<List<ConsumerGiftBillDto>> getConsumerGiftBillList(
            @RequestParam(value = "consumerDigitId", required = false) String consumerDigitId,
            @RequestParam(value = "consumerNick", required = false) String consumerNick,
            @RequestParam(value = "anchorDigitId", required = false) String anchorDigitId,
            @RequestParam(value = "anchorNick", required = false) String anchorNick,
            @RequestParam(value = "startTime", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date startTime,
            @RequestParam(value = "endTime", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date endTime,
            @RequestParam(value = "pageIndex", required = false, defaultValue = "1") int pageIndex,
            @RequestParam(value = "pageSize", required = false, defaultValue = "30") int pageSize) {
        Pair<List<ConsumerGiftBillDto>, Map<String,Object>> consumerGiftBillDtoList = profitManagerService
                .getConsumerGiftBillList(consumerDigitId, consumerNick,
                        anchorDigitId, anchorNick, startTime, endTime, pageIndex, pageSize);
        return ApiResp.succeed(consumerGiftBillDtoList.getFirst(), consumerGiftBillDtoList.getSecond());
    }
}
