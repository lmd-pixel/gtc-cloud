package com.fmisser.gtc.auth.service;

public interface SmsService {
    /**
     * 校验手机号和验证码
     * @param phone
     * @param code
     * @param type 验证类型 0：注册登录
     * @return
     */
    boolean checkPhoneCode(String phone, String code, int type);
}
