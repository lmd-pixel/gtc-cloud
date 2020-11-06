package com.fmisser.gtc.base.prop;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Data
@Component
@PropertySource(value = "classpath:config/jpush-conf.properties", encoding = "UTF-8")
//@ConfigurationProperties(prefix = "jpush")
public class JPushConfProp {
    @Value("${jpush.app_key}")
    private String appKey;

    @Value("${jpush.master_secret}")
    private String masterSecret;

    @Value("${jpush.verify.rsa.pri}")
    private String rsaPri;

    @Value("${jpush.sms.temeplate.id}")
    private int templateId;
}

