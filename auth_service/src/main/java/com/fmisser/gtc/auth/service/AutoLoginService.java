package com.fmisser.gtc.auth.service;

import com.fmisser.gtc.base.exception.ApiException;

public interface AutoLoginService {
    /**
     * 校验手机号一键登录token
     * 如果正确，返回手机号字符串
     */
    String checkPhoneToken(String phone, String token) throws ApiException;
}
