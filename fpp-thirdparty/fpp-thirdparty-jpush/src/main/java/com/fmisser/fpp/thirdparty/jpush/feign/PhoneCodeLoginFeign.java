package com.fmisser.fpp.thirdparty.jpush.feign;

import com.fmisser.fpp.thirdparty.jpush.dto.PhoneCodeGetReq;
import com.fmisser.fpp.thirdparty.jpush.dto.PhoneCodeGetResp;
import com.fmisser.fpp.thirdparty.jpush.dto.PhoneCodeVerifyReq;
import com.fmisser.fpp.thirdparty.jpush.dto.PhoneCodeVerifyResp;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * @author fmisser
 * @create 2021-05-10 下午9:28
 * @description 短信验证码登录
 */

@FeignClient(url = "https://api.sms.jpush.cn/v1", name = "jpush-phone-code-login")
public interface PhoneCodeLoginFeign {
    // 发送文本短信
    @PostMapping(value = "/codes", produces = MediaType.APPLICATION_JSON_VALUE)
    PhoneCodeGetResp getPhoneCode(@RequestHeader("Authorization") String basicAuth,
                                  @RequestBody PhoneCodeGetReq req);

    // 验证短信
    @PostMapping(value = "/codes/{msg_id}/valid", produces = MediaType.APPLICATION_JSON_VALUE)
    PhoneCodeVerifyResp verifyPhoneCode(@RequestHeader("Authorization") String basicAuth,
                                        @PathVariable("msg_id")  String msgId,
                                        @RequestBody PhoneCodeVerifyReq req);
}
