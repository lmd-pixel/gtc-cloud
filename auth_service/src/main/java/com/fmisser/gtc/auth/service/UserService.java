package com.fmisser.gtc.auth.service;

import com.fmisser.gtc.auth.domain.User;
import com.fmisser.gtc.base.dto.auth.TokenDto;
import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.base.response.ApiResp;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    /**
     * 账户密码注册
     */
    User create(String username, String password);

    /**
     * 手机号一键登录
     */
    ApiResp<TokenDto> autoLogin(String phone, String token) throws ApiException;

    /**
     * 手机号验证码登录
     */
    ApiResp<TokenDto> smsLogin(String phone, String code) throws ApiException;

    /**
     * 账号密码登录
     */
    ApiResp<TokenDto> login(String username, String password) throws ApiException;
}
