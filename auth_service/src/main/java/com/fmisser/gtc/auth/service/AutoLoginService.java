package com.fmisser.gtc.auth.service;

import com.fmisser.gtc.base.exception.ApiException;

public interface AutoLoginService {
    /**
     * 校验手机号一键登录token
     */
    boolean checkPhoneToken(String phone, String token) throws ApiException;
}
