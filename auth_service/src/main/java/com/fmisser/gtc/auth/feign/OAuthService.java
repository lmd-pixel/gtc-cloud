package com.fmisser.gtc.auth.feign;

import com.fmisser.gtc.base.dto.auth.TokenDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 调用内部的oauth认证
 */
@FeignClient(name = "auth")
@Service
public interface OAuthService {
    @PostMapping(value = "/oauth/token")
    TokenDto autoLogin(@RequestHeader("Authorization") String basicAuth,
                              @RequestParam("phone") String phone,
                              @RequestParam("token") String token,
                              @RequestParam("scope") String scope,
                              @RequestParam(value = "grant_type") String grant_type);

    @PostMapping(value = "/oauth/token")
    TokenDto smsLogin(@RequestHeader("Authorization") String basicAuth,
                       @RequestParam("phone") String phone,
                       @RequestParam("code") String code,
                       @RequestParam("scope") String scope,
                       @RequestParam(value = "grant_type") String grant_type);

    @PostMapping(value = "/oauth/token")
    TokenDto login(@RequestHeader("Authorization") String basicAuth,
                       @RequestParam("username") String username,
                       @RequestParam("password") String password,
                       @RequestParam("scope") String scope,
                       @RequestParam(value = "grant_type") String grant_type);
}
