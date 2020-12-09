package com.fmisser.gtc.auth.feign;

import com.fmisser.gtc.base.dto.jpush.LoginTokenVerifyDto;
import com.fmisser.gtc.base.dto.jpush.RequestLoginTokenVerifyDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 极光手机号登录认证接口调用
 */

@FeignClient(url = "https://api.verification.jpush.cn/v1/web", name = "jpush-verify")
@Service
public interface JPushVerifyFeign {
    @PostMapping(value = "/loginTokenVerify", produces = MediaType.APPLICATION_JSON_VALUE)
    LoginTokenVerifyDto loginTokenVerify(@RequestHeader("Authorization") String basicAuth,
                                         @RequestBody RequestLoginTokenVerifyDto requestLoginTokenVerifyDto);
}
