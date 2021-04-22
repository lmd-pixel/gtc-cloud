package com.fmisser.gtc.base.prop;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Data
@Configuration
@PropertySource(value = "classpath:config/im-conf-${spring.profiles.active}.properties", encoding = "UTF-8")
public class ImConfProp {

    @Value("${tencent.im.sdkappid}")
    private long sdkAppId;

    @Value("${tencent.im.key}")
    private String key;

    @Value("${tencent.im.expire}")
    private long expire;

    @Value("${tencent.im.admin}")
    private String admin;

    @Value("${tencent.api.appId}")
    private String apiAppId;

    @Value("${tencent.api.secretId}")
    private String secretId;

    @Value("${tencent.api.secretKey}")
    private String secretKey;
}
