package com.fmisser.gtc.base.prop;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Data
@Configuration
@PropertySource(value = "classpath:config/oauth-conf-${base.profiles.active}.properties", encoding = "UTF-8")
public class OauthConfProp {
    @Value("${oauth2.client}")
    private String oauth2Client;

    @Value("${oauth2.client.secret}")
    private String oauth2ClientSecret;

    @Value("${oauth2.scope}")
    private String oauth2Scope;

//    @Value("${oauth2.jwt.sign_key}")
//    private String oauth2JwtKey;
}
