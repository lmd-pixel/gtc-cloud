package com.fmisser.gtc.base.prop;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Data
@Configuration
@PropertySource(value = "classpath:config/apple-conf.properties", encoding = "UTF-8")
public class AppleConfProp {

    @Value("${apple.iap.sandbox.verify.url}")
    private String sandBoxVerifyUrl;

    @Value("${apple.iap.prod.verify.url}")
    private String prodVerifyUrl;
}
