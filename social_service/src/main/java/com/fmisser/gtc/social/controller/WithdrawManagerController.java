package com.fmisser.gtc.social.controller;

import com.fmisser.gtc.base.dto.social.PayAuditDto;
import com.fmisser.gtc.base.dto.social.WithdrawAuditDto;
import com.fmisser.gtc.base.response.ApiResp;
import com.fmisser.gtc.social.service.WithdrawManagerService;
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

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Api(description = "提现管理")
@RestController
@RequestMapping("/withdraw-manager")
@Validated
public class WithdrawManagerController {
    private final WithdrawManagerService withdrawManagerService;

    public WithdrawManagerController(WithdrawManagerService withdrawManagerService) {
        this.withdrawManagerService = withdrawManagerService;
    }

    @ApiOperation(value = "获取提现审核列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "String", paramType = "header"),
            @ApiImplicitParam(name = "digitId", value = "用户ID", paramType = "query"),
            @ApiImplicitParam(name = "nick", value = "用户昵称", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "起始时间", paramType = "query", dataType = "date"),
            @ApiImplicitParam(name = "endTime", value = "结束时间", paramType = "query", dataType = "date"),
            @ApiImplicitParam(name = "pageIndex", value = "展示第几页", paramType = "query", defaultValue = "1", dataType = "Integer"),
            @ApiImplicitParam(name = "pageSize", value = "每页数据条数", paramType = "query", defaultValue = "30", dataType = "Integer")
    })
    @PreAuthorize("hasAnyRole('MANAGER')")
    @RequestMapping(value = "/list-withdraw-audit", method = RequestMethod.GET)
    public ApiResp<List<WithdrawAuditDto>> getWithdrawAuditList(
            @RequestParam(value = "digitId", required = false) String digitId,
            @RequestParam(value = "nick", required = false) String nick,
            @RequestParam(value = "startTime", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date startTime,
            @RequestParam(value = "endTime", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date endTime,
            @RequestParam(value = "pageIndex", required = false, defaultValue = "1") int pageIndex,
            @RequestParam(value = "pageSize", required = false, defaultValue = "30") int pageSize) {
        Pair<List<WithdrawAuditDto>, Map<String, Object>> withdrawAuditDtoList =
                withdrawManagerService.getWithdrawAuditList(digitId, nick, startTime, endTime, pageIndex, pageSize);
        return ApiResp.succeed(withdrawAuditDtoList.getFirst(), withdrawAuditDtoList.getSecond());
    }

    @ApiOperation(value = "获取打款审核列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "String", paramType = "header"),
            @ApiImplicitParam(name = "digitId", value = "用户ID", paramType = "query"),
            @ApiImplicitParam(name = "nick", value = "用户昵称", paramType = "query"),
            @ApiImplicitParam(name = "type", value = "打款账户类型 0：支付宝， 1：银行卡", paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "startTime", value = "起始时间", paramType = "query", dataType = "date"),
            @ApiImplicitParam(name = "endTime", value = "结束时间", paramType = "query", dataType = "date"),
            @ApiImplicitParam(name = "pageIndex", value = "展示第几页", paramType = "query", defaultValue = "1", dataType = "Integer"),
            @ApiImplicitParam(name = "pageSize", value = "每页数据条数", paramType = "query", defaultValue = "30", dataType = "Integer")
    })
    @PreAuthorize("hasAnyRole('MANAGER')")
    @RequestMapping(value = "/list-pay-audit", method = RequestMethod.GET)
    public ApiResp<List<PayAuditDto>> getPayAuditList(
            @RequestParam(value = "digitId", required = false) String digitId,
            @RequestParam(value = "nick", required = false) String nick,
            @RequestParam(value = "type", required = false) Integer type,
            @RequestParam(value = "startTime", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date startTime,
            @RequestParam(value = "endTime", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date endTime,
            @RequestParam(value = "pageIndex", required = false, defaultValue = "1") int pageIndex,
            @RequestParam(value = "pageSize", required = false, defaultValue = "30") int pageSize) {
        Pair<List<PayAuditDto>, Map<String, Object>> payAuditDtoList =
                withdrawManagerService.getPayAuditList(digitId, nick, type, startTime, endTime, pageIndex, pageSize);
        return ApiResp.succeed(payAuditDtoList.getFirst(), payAuditDtoList.getSecond());
    }

    @ApiOperation(value = "审核操作")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "String", paramType = "header"),
            @ApiImplicitParam(name = "orderNumber", value = "订单号", paramType = "query", required = true),
            @ApiImplicitParam(name = "operate", value = "审核操作 0：不通过， 1：通过", paramType = "query", defaultValue = "0", dataType = "Integer"),
            @ApiImplicitParam(name = "message", value = "备注信息", paramType = "query")
    })
    @PreAuthorize("hasAnyRole('MANAGER')")
    @RequestMapping(value = "/withdraw-audit", method = RequestMethod.POST)
    public ApiResp<Integer> withdrawAudit(@RequestParam(value = "orderNumber") String orderNumber,
                                          @RequestParam(value = "operate") @Range(min = 0, max = 1, message = "参数范围不合法!") int operate,
                                          @RequestParam(value = "message", required = false) String message) {
        int ret = withdrawManagerService.withdrawAudit(orderNumber, operate, message);
        return ApiResp.succeed(ret);
    }

    @ApiOperation(value = "打款操作")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "String", paramType = "header"),
            @ApiImplicitParam(name = "orderNumber", value = "订单号", paramType = "query", required = true),
            @ApiImplicitParam(name = "operate", value = "打款操作 0：打款失败， 1：打款成功， 2： 部分打款（保留）", paramType = "query", defaultValue = "0", dataType = "Integer"),
            @ApiImplicitParam(name = "payActual", value = "实际打款金额, 如果是部分打款，此参数必须", paramType = "query", dataType = "number"),
            @ApiImplicitParam(name = "message", value = "备注信息", paramType = "query")
    })
    @PreAuthorize("hasAnyRole('MANAGER')")
    @RequestMapping(value = "/pay-audit", method = RequestMethod.POST)
    public ApiResp<Integer> payAudit(@RequestParam(value = "orderNumber") String orderNumber,
                                     @RequestParam(value = "operate") @Range(min = 0, max = 1, message = "参数范围不合法!") int operate,
                                     @RequestParam(value = "payActual", required = false) /*@DecimalMin(value = "0")*/ Double payActual,
                                     @RequestParam(value = "message", required = false) String message) {

        if (operate == 2 && Objects.isNull(payActual)) {
            ApiResp.failed(-1, "部分打款时实际付款金额不能为空");
        }

        int ret = withdrawManagerService.payAudit(orderNumber, operate, payActual, message);
        return ApiResp.succeed(ret);
    }

    @ApiOperation(value = "获取提现记录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "String", paramType = "header"),
            @ApiImplicitParam(name = "digitId", value = "用户ID", paramType = "query"),
            @ApiImplicitParam(name = "nick", value = "用户昵称", paramType = "query"),
            @ApiImplicitParam(name = "status", value = "提现状态： 0:待审核 1:待打款 2: 审核未通过 3：打款完成 4：全部（默认）", paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "startTime", value = "起始时间", paramType = "query", dataType = "date"),
            @ApiImplicitParam(name = "endTime", value = "结束时间", paramType = "query", dataType = "date"),
            @ApiImplicitParam(name = "pageIndex", value = "展示第几页", paramType = "query", defaultValue = "1", dataType = "Integer"),
            @ApiImplicitParam(name = "pageSize", value = "每页数据条数", paramType = "query", defaultValue = "30", dataType = "Integer")
    })
    @PreAuthorize("hasAnyRole('MANAGER')")
    @RequestMapping(value = "/list-withdraw", method = RequestMethod.GET)
    public ApiResp<List<WithdrawAuditDto>> getWithdrawList(
            @RequestParam(value = "digitId", required = false) String digitId,
            @RequestParam(value = "nick", required = false) String nick,
            @RequestParam(value = "status", required = false, defaultValue = "4") @Range(min = 0, max = 4) Integer status,
            @RequestParam(value = "startTime", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date startTime,
            @RequestParam(value = "endTime", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date endTime,
            @RequestParam(value = "pageIndex", required = false, defaultValue = "1") int pageIndex,
            @RequestParam(value = "pageSize", required = false, defaultValue = "30") int pageSize) {
        Pair<List<WithdrawAuditDto>, Map<String, Object>> withdrawAuditDtoList =
                withdrawManagerService.getWithdrawList(digitId, nick, startTime, endTime, status, pageIndex, pageSize);
        return ApiResp.succeed(withdrawAuditDtoList.getFirst(), withdrawAuditDtoList.getSecond());
    }
}
