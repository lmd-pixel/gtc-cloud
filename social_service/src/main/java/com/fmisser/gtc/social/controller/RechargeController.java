package com.fmisser.gtc.social.controller;

import com.fmisser.gtc.social.service.RechargeService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(description = "支付API")
@RestController
@RequestMapping("/pay")
@Validated
public class RechargeController {

    private final RechargeService rechargeService;

    public RechargeController(RechargeService rechargeService) {
        this.rechargeService = rechargeService;
    }

    @GetMapping(value = "/setIapCertificate")
    public String setIapCertificate(@RequestParam("userId") String userId,
                                    @RequestParam("receipt") String receipt,
                                    @RequestParam("chooseEnv") boolean chooseEnv) {

        if (StringUtils.isEmpty(userId) || StringUtils.isEmpty(receipt)) {
            return "failed";
        }

        try {
            return rechargeService.IapVerifyReceipt(userId, receipt, chooseEnv);
        } catch (Exception e) {
            // TODO: 2020/12/7 记录错误
            return "failed";
        }
    }
}
