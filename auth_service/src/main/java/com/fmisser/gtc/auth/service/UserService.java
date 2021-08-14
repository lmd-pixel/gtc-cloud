package com.fmisser.gtc.auth.service;

import com.fmisser.gtc.auth.domain.User;
import com.fmisser.gtc.base.dto.auth.TokenDto;
import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.base.response.ApiResp;
import io.swagger.annotations.Api;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    /**
     * 账户密码注册
     * @param username
     * @param password
     * @param roleName
     * @return
     */
    User create(String username, String password, String roleName) throws ApiException;

    User editUser(String username, String password, String roleName) throws ApiException;

    int enableUser(String username, int enable) throws ApiException;

    int deleteUser(String username) throws ApiException;

    /**
     * 手机号一键登录
     */
    TokenDto autoLogin(String identity, String token,String ipAddress,String deviceId) throws ApiException;

    /**
     * 手机号验证码登录
     */
    TokenDto smsLogin(String phone, String code,String ipAddress,String deviceId) throws ApiException;

    /**
     * 账号密码登录
     */
    TokenDto login(String username, String password) throws ApiException;

    /**
     * 苹果一键登录
     */
    TokenDto appleLogin(String subject, String token) throws ApiException;

    /**
     * wx 登录
     */
    TokenDto wxLogin(String unionid) throws ApiException;

    /**
     * 通过refresh token 刷新 token
     */
    TokenDto refreshToken(String refreshToken) throws ApiException;

    /**
     * 用户登出
     */
    int logout(User user) throws ApiException;
}
