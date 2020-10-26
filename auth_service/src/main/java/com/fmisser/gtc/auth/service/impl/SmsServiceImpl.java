package com.fmisser.gtc.auth.service.impl;

import com.fmisser.gtc.auth.service.SmsService;
import org.springframework.stereotype.Service;

@Service
public class SmsServiceImpl implements SmsService {
    @Override
    public boolean checkPhoneCode(String phone, String code, int type) {
        // 验证手机号验证码
        return true;
    }
}
