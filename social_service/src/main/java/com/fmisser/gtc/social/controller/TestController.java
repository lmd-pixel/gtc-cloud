package com.fmisser.gtc.social.controller;

import com.fmisser.fpp.oss.abs.service.OssService;
import com.fmisser.fpp.thirdparty.apple.feign.AppleIdLoginGetAuthKeysFeign;
import com.fmisser.fpp.thirdparty.jpush.dto.PhoneCodeGetReq;
import com.fmisser.fpp.thirdparty.jpush.feign.PhoneCodeLoginFeign;
import com.fmisser.gtc.social.mq.WxWebHookBinding;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(description = "TEST API")
@RequestMapping("/test")
@RestController
@AllArgsConstructor
public class TestController {

    private final WxWebHookBinding wxWebHookBinding;
    private final OssService ossService;
    private final AppleIdLoginGetAuthKeysFeign appleIdLoginGetAuthKeysFeign;
    private final PhoneCodeLoginFeign phoneCodeLoginFeign;


    @GetMapping("/slow")
    public Object slowTest() {
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "ok";
    }

    @PostMapping("/post-msg")
    public Object postMsg() {
        String message = String.format("1,%d,%d", 933, 22);
        Message<String> tipMsg = MessageBuilder.withPayload(message).build();
        return wxWebHookBinding.wxWebHookOutputChannel().send(tipMsg);
    }

    @ApiOperation("fpp-test")
    @GetMapping("fpp-test")
    public String fppTest() {
        return ossService.getName();
    }

    @ApiOperation("fpp-apple")
    @GetMapping("fpp-apple")
    public Object fppAppleTest() {
        return appleIdLoginGetAuthKeysFeign.getAuthKeys();
    }

    @ApiOperation("fpp-jpush")
    @GetMapping("fpp-jpush")
    public Object fppJPushTest() {
        return phoneCodeLoginFeign.getPhoneCode("1", new PhoneCodeGetReq("18058159956", 1));
    }
}
