package com.fmisser.gtc.base.prop;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Data
@Configuration
@PropertySource(value = "classpath:config/im-conf.properties", encoding = "UTF-8")
public class ImConfProp {

    @Value("${tencent.im.sdkappid}")
    private long sdkAppId;

    @Value("${tencent.im.key}")
    private String key;

    @Value("${tencent.im.expire}")
    private long expire;

    @Value("${tencent.im.admin}")
    private String admin;
}
