package com.fmisser.gtc.auth.service;

public interface SmsService {
    /**
     * 校验手机号和验证码
     * @param type 验证类型 0：注册登录
     */
    boolean checkPhoneCode(String phone, String code, int type);

    /**
     * 发送验证码
     */
    boolean sendPhoneCode(String phone, int type);
}
