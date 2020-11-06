package com.fmisser.gtc.auth.service.impl;

import com.fmisser.gtc.auth.feign.JPushVerifyService;
import com.fmisser.gtc.auth.service.AutoLoginService;
import com.fmisser.gtc.base.dto.jpush.LoginTokenVerifyDto;
import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.base.prop.JPushConfProp;
import com.fmisser.gtc.base.utils.JPushUtils;
import org.springframework.stereotype.Service;

@Service
public class AutoLoginServiceImpl implements AutoLoginService {

    private final JPushConfProp jPushConfProp;

    private final JPushVerifyService jPushVerifyService;


    public AutoLoginServiceImpl(JPushConfProp jPushConfProp,
                                JPushVerifyService jPushVerifyService) {
        this.jPushConfProp = jPushConfProp;
        this.jPushVerifyService = jPushVerifyService;
    }

    @Override
    public boolean checkPhoneToken(String phone, String token) throws ApiException {
        String basicAuthString = JPushUtils.genAuthString(jPushConfProp.getAppKey(), jPushConfProp.getMasterSecret());
        LoginTokenVerifyDto loginTokenVerifyDto = jPushVerifyService.loginTokenVerify(basicAuthString, token, phone);
        if (loginTokenVerifyDto == null) {
            return false;
        }

        if (loginTokenVerifyDto.getCode() == 8000) {
            // TODO: 2020/11/6 记录用户认证到数据库

            return true;
        } else {
            // TODO: 2020/11/6 记录错误信息
            return false;
        }
    }
}
