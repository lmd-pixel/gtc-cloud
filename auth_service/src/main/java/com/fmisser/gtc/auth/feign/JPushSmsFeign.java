package com.fmisser.gtc.auth.feign;

import com.fmisser.gtc.base.dto.jpush.PhoneCodeDto;
import com.fmisser.gtc.base.dto.jpush.PhoneCodeVerifyDto;
import com.fmisser.gtc.base.dto.jpush.RequestCodeDto;
import com.fmisser.gtc.base.dto.jpush.RequestVerifyCodeDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

/**
 * 极光短信验证码相关接口
 */

@FeignClient(url = "https://api.sms.jpush.cn/v1", name = "jpush-sms")
@Service
public interface JPushSmsFeign {
    // 发送文本短信
    @PostMapping(value = "/codes", produces = MediaType.APPLICATION_JSON_VALUE)
    PhoneCodeDto sendPhoneCode(@RequestHeader("Authorization") String basicAuth,
                               @RequestBody RequestCodeDto requestCodeDto);

    // 验证短信
    @PostMapping(value = "/codes/{msg_id}/verify", produces = MediaType.APPLICATION_JSON_VALUE)
    PhoneCodeVerifyDto verifyPhoneCode(@RequestHeader("Authorization") String basicAuth,
                                              @PathVariable("msg_id")  String msgId,
                                              @RequestBody RequestVerifyCodeDto requestVerifyCodeDto);
}
