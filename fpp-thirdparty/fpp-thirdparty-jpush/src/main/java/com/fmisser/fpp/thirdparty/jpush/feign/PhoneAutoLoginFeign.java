package com.fmisser.fpp.thirdparty.jpush.feign;

import com.fmisser.fpp.thirdparty.jpush.dto.AutoLoginVerifyReq;
import com.fmisser.fpp.thirdparty.jpush.dto.AutoLoginVerifyResp;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * @author fmisser
 * @create 2021-05-10 下午9:26
 * @description 手机号一键登录
 */
@FeignClient(url = "https://api.verification.jpush.cn/v1/web", name = "jpush-phone-auto-login")
public interface PhoneAutoLoginFeign {
    // 验证 一键登录的 token
    @PostMapping(value = "/loginTokenVerify", produces = MediaType.APPLICATION_JSON_VALUE)
    AutoLoginVerifyResp autoLoginTokenVerify(@RequestHeader("Authorization") String basicAuth,
                                             @RequestBody AutoLoginVerifyReq req);
}
