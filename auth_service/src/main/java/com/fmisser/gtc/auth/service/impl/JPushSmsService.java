package com.fmisser.gtc.auth.service.impl;

import com.fmisser.gtc.auth.domain.PhoneCodeRequest;
import com.fmisser.gtc.auth.feign.JPushSmsFeign;
import com.fmisser.gtc.auth.repository.PhoneCodeRequestRepository;
import com.fmisser.gtc.auth.service.SmsService;
import com.fmisser.gtc.base.dto.auth.PhoneCodeRequestDto;
import com.fmisser.gtc.base.dto.jpush.PhoneCodeDto;
import com.fmisser.gtc.base.dto.jpush.PhoneCodeVerifyDto;
import com.fmisser.gtc.base.dto.jpush.RequestCodeDto;
import com.fmisser.gtc.base.dto.jpush.RequestVerifyCodeDto;
import com.fmisser.gtc.base.prop.JPushConfProp;
import com.fmisser.gtc.base.utils.JPushUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Objects;

/**
 * 目前用的极光
 * step1： 调用极光请求到验证码，返回msg_id
 * step2： 验证验证码需要拿msg_id 和 code 去调用极光api 验证
 */

@Service
public class JPushSmsService implements SmsService {

    private final JPushSmsFeign jPushSmsFeign;

    private final JPushConfProp jPushConfProp;

    private PhoneCodeRequestRepository phoneCodeRequestRepository;

    public JPushSmsService(JPushSmsFeign jPushSmsFeign,
                           JPushConfProp jPushConfProp,
                           PhoneCodeRequestRepository phoneCodeRequestRepository) {
        this.jPushSmsFeign = jPushSmsFeign;
        this.jPushConfProp = jPushConfProp;
        this.phoneCodeRequestRepository = phoneCodeRequestRepository;
    }

    // 验证手机号验证码
    @Override
    public boolean checkPhoneCode(String phone, String code, int type) {

        // TODO: 2020/11/20 测试代码 code 使用888888 直接pass
        if (code.equals("888888")) {
            return true;
        }

        // 获取本地存储的 msg id
        PhoneCodeRequestDto phoneCodeRequestDto =
                phoneCodeRequestRepository.findTopByPhoneAndTypeOrderByCreateTimeDesc(phone, type);

        if (phoneCodeRequestDto == null) {
            return false;
        }

        // 调用 jpush rest api去认证
        String basicAuthString = JPushUtils
                .genAuthString(jPushConfProp.getAppKey(), jPushConfProp.getMasterSecret());
        RequestVerifyCodeDto requestVerifyCodeDto = new RequestVerifyCodeDto(code);
        System.out.println("basicAuthString:" + basicAuthString);
        PhoneCodeVerifyDto phoneCodeVerifyDto =
                jPushSmsFeign.verifyPhoneCode(basicAuthString, phoneCodeRequestDto.getMsgId(), requestVerifyCodeDto);

        if (phoneCodeVerifyDto == null || !phoneCodeVerifyDto.getIs_valid()) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public boolean sendPhoneCode(String phone, int type) {
        String basicAuthString = JPushUtils
                .genAuthString(jPushConfProp.getAppKey(), jPushConfProp.getMasterSecret());
        RequestCodeDto requestCodeDto = new RequestCodeDto(phone, jPushConfProp.getTemplateId());
        PhoneCodeDto phoneCodeDto = jPushSmsFeign.sendPhoneCode(basicAuthString, requestCodeDto);
        if (phoneCodeDto == null) {
            return false;
        }

        if (!StringUtils.isEmpty(phoneCodeDto.getMsg_id())) {
            // 如果有msg id, 则认为是正确
            // save to db
            PhoneCodeRequest phoneCodeRequest = new PhoneCodeRequest();
            phoneCodeRequest.setId(0L);
            phoneCodeRequest.setPhone(phone);
            phoneCodeRequest.setMsgId(phoneCodeDto.getMsg_id());
            phoneCodeRequest.setType(type);

            phoneCodeRequestRepository.save(phoneCodeRequest);

            return true;
        }

        if (Objects.nonNull(phoneCodeDto.getError()) &&
                phoneCodeDto.getError().getCode() != 50000) {
            // TODO: 2020/11/6 记录错误

        }

        return false;
    }
}
