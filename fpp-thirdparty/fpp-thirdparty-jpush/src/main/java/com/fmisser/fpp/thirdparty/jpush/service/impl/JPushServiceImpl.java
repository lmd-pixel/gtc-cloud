package com.fmisser.fpp.thirdparty.jpush.service.impl;

import com.fmisser.fpp.thirdparty.jpush.dto.AutoLoginVerifyReq;
import com.fmisser.fpp.thirdparty.jpush.dto.AutoLoginVerifyResp;
import com.fmisser.fpp.thirdparty.jpush.feign.PhoneAutoLoginFeign;
import com.fmisser.fpp.thirdparty.jpush.prop.JPushAppProperty;
import com.fmisser.fpp.thirdparty.jpush.prop.JPushExtensionProperty;
import com.fmisser.fpp.thirdparty.jpush.service.JPushService;
import com.fmisser.fpp.thirdparty.jpush.utils.JPushUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Objects;

/**
 * @author fmisser
 * @create 2021-05-13 下午4:06
 * @description
 */
@Slf4j
@Service
@AllArgsConstructor
public class JPushServiceImpl implements JPushService {
    private final PhoneAutoLoginFeign phoneAutoLoginFeign;
    private final JPushExtensionProperty jPushExtensionProperty;

    @Override
    public String verifyLoginToken(String identity, String loginToken) throws RuntimeException {
        log.info("[jpush] verify login token with identity: {} and loginToken: {}.", identity, loginToken);

        JPushAppProperty jPushAppProperty = getJPushPropertyFromIdentity(identity);
        if (Objects.isNull(jPushAppProperty)) {
            log.error("[jpush] verify login token get property from identity: {} is null.", identity);
            throw new RuntimeException("认证配置信息错误");
        }

        String basicAuthString = JPushUtils.genAuthString(jPushAppProperty.getAppKey(), jPushAppProperty.getMasterSecret());
        AutoLoginVerifyReq req = new AutoLoginVerifyReq(loginToken, identity);

        AutoLoginVerifyResp resp;
        try {
            resp = phoneAutoLoginFeign.autoLoginTokenVerify(basicAuthString, req);
        } catch (Exception e) {
            log.error("[jpush] verify login token request exception: {}", e.getMessage());
            throw new RuntimeException("认证请求出错");
        }

        if (Objects.isNull(resp)) {
            log.error("[jpush] login token verify response is null");
            throw new RuntimeException("认证结果异常");
        }

        if (resp.getCode() != 8000) {
            // 只有8000是正确结果, 其他表示错误, 详细错误原因对照jpush的错误码
            log.error("[jpush] login token verify failed with code: {}", resp.getCode());
            throw new RuntimeException("认证失败");
        }

        String phone = resp.getPhone();
        log.info("[jpush] login token verify response encode phone is: {}", phone);
        if (StringUtils.isEmpty(phone)) {
            log.error("[jpush] login token verify response phone is empty");
            throw new RuntimeException("认证结果错误");
        }

        try {
            String decodePhone= JPushUtils.rsaDecrypt(phone, jPushAppProperty.getAutoLogin().getRsaPri());
            log.info("[jpush] login token verify response phone decode value: {}", decodePhone);
            return decodePhone;
        } catch (Exception e) {
            log.error("[jpush] login token verify response phone decode failed: {}", e.getMessage());
            throw new RuntimeException("认证结果解密出错");
        }
    }

    /**
     * 通过 identity 查找对应的属性, identity 对应 一键登录里的 Android package name 或者 iOS 的 bundle id
     */
    protected JPushAppProperty getJPushPropertyFromIdentity(String identity) {
        return jPushExtensionProperty.getApps().values().parallelStream()
                .filter(jPushAppProperty ->
                        jPushAppProperty.getAutoLogin().getBundleId().equals(identity) ||
                                jPushAppProperty.getAutoLogin().getPackageName().equals(identity))
                .findFirst()
                .orElse(null);
    }
}
