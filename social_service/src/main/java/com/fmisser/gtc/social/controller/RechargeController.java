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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
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

    @PostMapping(value = "/create-order")
    public ApiResp<String> createOrder(@RequestParam("productName") String productName) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getPrincipal().toString();
        User userDo = userService.getUserByUsername(username);

        return ApiResp.succeed(rechargeService.createOrder(userDo, productName));
    }

    @PostMapping(value = "/update-order")
    public ApiResp<String> updateOrder(@RequestParam("orderNumber") String orderNumber,
                                       @RequestParam("status") Integer status) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getPrincipal().toString();
        User userDo = userService.getUserByUsername(username);

        return ApiResp.succeed(rechargeService.updateOrder(userDo, orderNumber, status));
    }

    @PostMapping(value = "/complete-order-iap")
    public ApiResp<String> completeOrderIap(@RequestParam(value = "orderNumber", required = false) String orderNumber,
                                            @RequestParam(value = "env", required = false, defaultValue = "1") int env,
                                            @RequestParam("receipt") String receipt,
                                            @RequestParam("productId") String productId,
                                            @RequestParam("transactionId") String transactionId,
                                            @RequestParam("purchaseDate") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date purchaseDate) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getPrincipal().toString();
        User userDo = userService.getUserByUsername(username);

        return ApiResp.succeed(rechargeService.completeIapOrder(userDo, orderNumber,
                env, receipt, productId, transactionId, purchaseDate));
    }

    @PostMapping(value = "/iap-receipt-verify")
    public ApiResp<String> setIapCertificate(@RequestParam("receipt") String receipt,
                                             @RequestParam(value = "env", required = false, defaultValue = "0") int env,
                                             @RequestParam(value = "env_ex", required = false, defaultValue = "1") int env_ex,
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
                env_ex,
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

    @ApiOperation("获取充值次数")
    @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "String", paramType = "header")
    @PostMapping(value = "/total-count")
    public ApiResp<Long> getRechargeTotalCount() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getPrincipal().toString();
        User userDo = userService.getUserByUsername(username);

        Long count = rechargeService.getUserRechargeCount(userDo);
        return ApiResp.succeed(count);
    }

    @ApiOperation("支付服务上分")
    @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "String", paramType = "header")
    @PreAuthorize("hasRole('PAY_SERVER')")
    @PostMapping("/update-coin")
    public ApiResp<Long> payServer(@RequestParam("userId") String userId,
                                   @RequestParam(value = "inviteId", required = false) String inviteId,
                                   @RequestParam("orderNumber") String orderNumber,
                                   @RequestParam("productId") Long productId,
                                   @RequestParam("coin") BigDecimal coin,
                                   @RequestParam("price") BigDecimal price,
                                   @RequestParam("pay") BigDecimal pay,
                                   @RequestParam("currency") String currency) {

        User user = userService.getUserByDigitId(userId);
        User inviteUser = null;
        if (!StringUtils.isEmpty(inviteId)) {
            inviteUser = userService.getAnchorByDigitIdPeace(inviteId);
        }
        long ret = rechargeService.completePayOrder(user, inviteUser, orderNumber,
                productId, coin, price, pay, currency);
        return ApiResp.succeed(ret);
    }

}
