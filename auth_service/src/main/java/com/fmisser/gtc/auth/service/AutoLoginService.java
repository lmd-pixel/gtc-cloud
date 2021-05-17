package com.fmisser.gtc.auth.service;

import com.fmisser.gtc.base.exception.ApiException;

public interface AutoLoginService {
    /**
     * jpush 校验手机号一键登录token
     * 如果正确，返回手机号字符串
     * @deprecated 当前版本只考虑一套 appid 的模式，无法使用多个 appid，
     * 老的包继续使用，新包使用 {@linkplain AutoLoginService#jpushVerifyLoginToken}
     */
    @Deprecated
    String checkPhoneToken(String phone, String token) throws ApiException;

    /**
     * 极光一键登录，支持多套 app id
     * @param identity 应用包名
     * @param loginToken 需要校验的token
     * @return  如果校验正确返回登录的手机号
     */
    String jpushVerifyLoginToken(String identity, String loginToken) throws ApiException;
}
