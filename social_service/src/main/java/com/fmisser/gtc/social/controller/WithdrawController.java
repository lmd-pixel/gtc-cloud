package com.fmisser.gtc.social.controller;

import com.fmisser.gtc.base.response.ApiResp;
import com.fmisser.gtc.social.domain.User;
import com.fmisser.gtc.social.domain.WithdrawAudit;
import com.fmisser.gtc.social.service.UserService;
import com.fmisser.gtc.social.service.WithdrawService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Api(description = "提现")
@RestController
@RequestMapping("/withdraw")
@Validated
public class WithdrawController {

    @Autowired
    private WithdrawService withdrawService;

    @Autowired
    private UserService userService;

    @ApiOperation(value = "申请提现")
    @PostMapping("/apply")
    public ApiResp<WithdrawAudit> applyWithdraw(@RequestParam("coin") String coin) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getPrincipal().toString();
        User userDo = userService.getUserByUsername(username);

        BigDecimal coinVal = BigDecimal.valueOf(Long.parseLong(coin));
        WithdrawAudit withdrawAudit = withdrawService.requestWithdraw(userDo, coinVal);
        return ApiResp.succeed(withdrawAudit);
    }

    @ApiOperation(value = "获取当前提现申请")
    @GetMapping("/curr")
    public ApiResp<WithdrawAudit> getCurrWithdraw() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getPrincipal().toString();
        User userDo = userService.getUserByUsername(username);

        WithdrawAudit withdrawAudit = withdrawService.getCurrWithdraw(userDo);
        return ApiResp.succeed(withdrawAudit);
    }

}
