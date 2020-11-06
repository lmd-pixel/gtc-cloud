package com.fmisser.gtc.auth.service.impl;

import com.fmisser.gtc.auth.domain.PhoneCodeRequest;
import com.fmisser.gtc.auth.feign.JPushSmsService;
import com.fmisser.gtc.auth.repository.PhoneCodeRequestRepository;
import com.fmisser.gtc.auth.service.SmsService;
import com.fmisser.gtc.base.dto.auth.PhoneCodeRequestDto;
import com.fmisser.gtc.base.dto.jpush.PhoneCodeDto;
import com.fmisser.gtc.base.dto.jpush.PhoneCodeVerifyDto;
import com.fmisser.gtc.base.prop.JPushConfProp;
import com.fmisser.gtc.base.utils.JPushUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SmsServiceImpl implements SmsService {

    private final JPushSmsService jPushSmsService;

    private final JPushConfProp jPushConfProp;

    private PhoneCodeRequestRepository phoneCodeRequestRepository;

    public SmsServiceImpl(JPushSmsService jPushSmsService,
                          JPushConfProp jPushConfProp,
                          PhoneCodeRequestRepository phoneCodeRequestRepository) {
        this.jPushSmsService = jPushSmsService;
        this.jPushConfProp = jPushConfProp;
        this.phoneCodeRequestRepository = phoneCodeRequestRepository;
    }

    // 验证手机号验证码
    @Override
    public boolean checkPhoneCode(String phone, String code, int type) {
        // 获取本地存储的 msg id
        PhoneCodeRequestDto phoneCodeRequestDto =
                phoneCodeRequestRepository.findByPhoneAndTypeOrderByCreateTimeDesc(phone, type);

        if (phoneCodeRequestDto == null) {
            return false;
        }

        // 调用 jpush rest api去认证
        String basicAuthString = JPushUtils.genAuthString(jPushConfProp.getAppKey(), jPushConfProp.getMasterSecret());
        PhoneCodeVerifyDto phoneCodeVerifyDto =
                jPushSmsService.verifyPhoneCode(basicAuthString, phoneCodeRequestDto.getMsgId(), code, jPushConfProp.getTemplateId());

        if (phoneCodeVerifyDto == null || !phoneCodeVerifyDto.is_valid()) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public boolean sendPhoneCode(String phone, int type) {
        String basicAuthString = JPushUtils.genAuthString(jPushConfProp.getAppKey(), jPushConfProp.getMasterSecret());
        PhoneCodeDto phoneCodeDto = jPushSmsService.sendPhoneCode(basicAuthString, phone, jPushConfProp.getTemplateId());
        if (phoneCodeDto == null) {
            return false;
        }

        if (phoneCodeDto.getError().getCode() == 50000) {

            // save to db
            PhoneCodeRequest phoneCodeRequest = new PhoneCodeRequest();
            phoneCodeRequest.setId(0L);
            phoneCodeRequest.setPhone(phone);
            phoneCodeRequest.setMsgId(phoneCodeDto.getMsg_id());
            phoneCodeRequest.setType(type);

            phoneCodeRequestRepository.save(phoneCodeRequest);

            return true;
        } else {
            // TODO: 2020/11/6 记录错误
            return false;
        }
    }
}
