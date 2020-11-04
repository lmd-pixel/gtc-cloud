package com.fmisser.gtc.notice.controller;

import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.base.response.ApiResp;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.exceptions.UnauthorizedClientException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/test", produces = "application/json;charset=UTF-8")
public class TestController {

    @GetMapping(value = "/demo1")
    ApiResp<String> demo1() {
//        return ApiResp.succeed("hello world!");
//        throw new ApiException(1001, "error test");
        throw new UnauthorizedClientException("认证失败");
    }
}
