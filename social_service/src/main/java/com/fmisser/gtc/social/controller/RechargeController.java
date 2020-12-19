package com.fmisser.gtc.social.controller;

import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.base.response.ApiResp;
import com.fmisser.gtc.social.domain.Product;
import com.fmisser.gtc.social.domain.User;
import com.fmisser.gtc.social.service.ConsumeService;
import com.fmisser.gtc.social.service.RechargeService;
import com.fmisser.gtc.social.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@Api(description = "支付API")
@RestController
@RequestMapping("/pay")
@Validated
public class RechargeController {

    private final RechargeService rechargeService;
    private final UserService userService;
    private final ConsumeService consumeService;

    public RechargeController(RechargeService rechargeService,
                              UserService userService,
                              ConsumeService consumeService) {
        this.rechargeService = rechargeService;
        this.userService = userService;
        this.consumeService = consumeService;
    }

    @PostMapping(value = "/iap-receipt-verify")
    public ApiResp<String> setIapCertificate(@RequestParam("receipt") String receipt,
                                             @RequestParam("env") int env,
                                             @RequestParam("productId") String productId,
                                             @RequestParam("transactionId") String transactionId,
                                             @RequestParam("purchaseDate") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date purchaseDate) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getPrincipal().toString();
        User userDo = userService.getUserByUsername(username);

        if (StringUtils.isEmpty(receipt)) {
            throw new ApiException(-1, "invalid receipt!");
        }

        String ret = rechargeService.IapVerifyReceipt(userDo, receipt,
                env,
                productId, transactionId, purchaseDate);
        return ApiResp.succeed(ret);
    }

    @ApiOperation("购买vip")
    @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "String", paramType = "header")
    @PostMapping(value = "/buy-vip")
    public ApiResp<Integer> buyVip() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getPrincipal().toString();
        User userDo = userService.getUserByUsername(username);

        int ret = consumeService.buyVip(userDo);
        return ApiResp.succeed(ret);
    }

}
