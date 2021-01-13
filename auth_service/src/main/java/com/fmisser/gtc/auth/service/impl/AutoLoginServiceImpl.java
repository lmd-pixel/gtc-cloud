package com.fmisser.gtc.auth.service.impl;

import com.fmisser.gtc.auth.domain.PhoneTokenRequest;
import com.fmisser.gtc.auth.feign.JPushVerifyFeign;
import com.fmisser.gtc.auth.repository.PhoneTokenRequestRepository;
import com.fmisser.gtc.auth.service.AutoLoginService;
import com.fmisser.gtc.base.dto.jpush.LoginTokenVerifyDto;
import com.fmisser.gtc.base.dto.jpush.RequestLoginTokenVerifyDto;
import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.base.prop.JPushConfProp;
import com.fmisser.gtc.base.utils.JPushUtils;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * 极光一键认证登录
 */
@Service
public class AutoLoginServiceImpl implements AutoLoginService {

    private final JPushConfProp jPushConfProp;

    private final JPushVerifyFeign jPushVerifyFeign;

    private final PhoneTokenRequestRepository phoneTokenRequestRepository;

    public AutoLoginServiceImpl(JPushConfProp jPushConfProp,
                                JPushVerifyFeign jPushVerifyFeign,
                                PhoneTokenRequestRepository phoneTokenRequestRepository) {
        this.jPushConfProp = jPushConfProp;
        this.jPushVerifyFeign = jPushVerifyFeign;
        this.phoneTokenRequestRepository = phoneTokenRequestRepository;
    }

    @Override
    @SneakyThrows
    public String checkPhoneToken(String phone, String token) throws ApiException {
        String basicAuthString = JPushUtils
                .genAuthString(jPushConfProp.getAppKey(), jPushConfProp.getMasterSecret());

        // phone 作为exID 传递
        RequestLoginTokenVerifyDto requestLoginTokenVerifyDto = new RequestLoginTokenVerifyDto(token, phone);

        // 先保存请求信息
        PhoneTokenRequest phoneTokenRequest = new PhoneTokenRequest();
//        phoneTokenRequest.setPhone(phone);
        phoneTokenRequest.setToken(token);
        phoneTokenRequest = phoneTokenRequestRepository.save(phoneTokenRequest);

        LoginTokenVerifyDto loginTokenVerifyDto =
                jPushVerifyFeign.loginTokenVerify(basicAuthString, requestLoginTokenVerifyDto);

        if (loginTokenVerifyDto == null) {
            throw new ApiException(-1, "获取数据异常，验证失败!");
        }

        // 继续记录到数据库
        phoneTokenRequest.setRequestId(loginTokenVerifyDto.getId());
        phoneTokenRequest.setExId(loginTokenVerifyDto.getExID());
        phoneTokenRequest.setCode(loginTokenVerifyDto.getCode());
        phoneTokenRequest.setContent(loginTokenVerifyDto.getContent());
        String encodePhone = loginTokenVerifyDto.getPhone();
        if (Objects.nonNull(encodePhone)) {
            phoneTokenRequest.setEncodePhone(encodePhone);
            String decodePhone = JPushUtils.rsaDecrypt(encodePhone, jPushConfProp.getRsaPri());
            // 前端不会传phone过来，只传token
            phoneTokenRequest.setPhone(decodePhone);
        }
        phoneTokenRequestRepository.save(phoneTokenRequest);

        if (loginTokenVerifyDto.getCode() == 8000) {

            // FIXME: 2021/1/8 前端不会传phone，不需要验证
            // 认证 phone 是否相同
//            String encodePhone = loginTokenVerifyDto.getPhone();
//            String decodePhone = JPushUtils.rsaDecrypt(encodePhone, jPushConfProp.getRsaPri());
//            if (!decodePhone.equals(loginTokenVerifyDto.getExID())) {
//                // error
//                throw new ApiException(-1, "手机号验证失败!");
//            }

            return phoneTokenRequest.getPhone();
        } else {
            throw new ApiException(-1, "认证失败!");
        }
    }
}
