package com.fmisser.gtc.auth.feign;

import com.fmisser.gtc.base.dto.jpush.PhoneCodeDto;
import com.fmisser.gtc.base.dto.jpush.PhoneCodeVerifyDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 极光短信验证码相关接口
 */

@FeignClient(url = "https://api.sms.jpush.cn/v1", name = "jpush-sms")
@Service
public interface JPushSmsService {
    // 发送文本短信
    @PostMapping(value = "/codes", produces = MediaType.APPLICATION_JSON_VALUE)
    public PhoneCodeDto sendPhoneCode(@RequestHeader("Authorization") String basicAuth,
                                      @RequestParam("mobile") String mobile,
                                      @RequestParam("temp_id") int temp_id);

    // 验证短信
    @PostMapping(value = "/codes/{msg_id}/verify", produces = MediaType.APPLICATION_JSON_VALUE)
    public PhoneCodeVerifyDto verifyPhoneCode(@RequestHeader("Authorization") String basicAuth,
                                              @PathVariable("msg_id")  String msgId,
                                              @RequestParam("code") String code,
                                              @RequestParam("temp_id") int temp_id);
}
