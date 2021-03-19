package com.fmisser.gtc.auth.controller;

import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.Map;

@Deprecated
//@Api(description = "登录代理中心")
@RestController
@RequestMapping("/proxy")
public class OAuthProxyController {
    @Autowired
    private TokenEndpoint tokenEndpoint;

    // 无法获得Principal principal对象，可以参考BasicAuthenticationConverter convert函数
    @RequestMapping(value = "/oauth/token", method= RequestMethod.GET)
    public ResponseEntity<OAuth2AccessToken> getAccessToken(
            HttpServletRequest request,
            Principal principal,
            @RequestParam Map<String, String> parameters) throws HttpRequestMethodNotSupportedException {
        return tokenEndpoint.getAccessToken(principal, parameters);
    }

    @RequestMapping(value = "/oauth/token", method=RequestMethod.POST)
    public ResponseEntity<OAuth2AccessToken> postAccessToken(
            HttpServletRequest request,
            Principal principal,
            @RequestParam Map<String, String> parameters) throws HttpRequestMethodNotSupportedException {
        return tokenEndpoint.postAccessToken(principal, parameters);
    }

    @GetMapping(value = "/block-0")
    public String testBlock0() throws InterruptedException {
        return "block-0";
    }

    @GetMapping(value = "/block-1")
    public String testBlock1() throws RuntimeException {
        throw new RuntimeException("wrong");
    }

    @GetMapping(value = "/block-5")
    public String testBlock5() throws InterruptedException {
        Thread.sleep(5000);
        return "block-5";
    }

    @GetMapping(value = "/block-10")
    public String testBlock10() throws InterruptedException {
        Thread.sleep(10000);
        return "block-10";
    }

    @GetMapping(value = "/block-15")
    public String testBlock15() throws InterruptedException {
        Thread.sleep(15000);
        return "block-15";
    }
}
