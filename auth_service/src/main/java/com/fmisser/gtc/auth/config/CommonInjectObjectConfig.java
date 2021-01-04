package com.fmisser.gtc.auth.config;

import com.fmisser.gtc.base.prop.OauthConfProp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

@Configuration
public class CommonInjectObjectConfig {

    @Autowired
    private OauthConfProp oauthConfProp;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter() {
        JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
        // 设置签名秘钥
        // TODO: 2020/10/26 字段改成配置
//        jwtAccessTokenConverter.setSigningKey("jwt-key");
        jwtAccessTokenConverter.setSigningKey(oauthConfProp.getOauth2JwtKey());
        return jwtAccessTokenConverter;
    }
}
