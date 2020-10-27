package com.fmisser.gtc.auth.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import javax.annotation.Resource;

/**
 * 基于内存存储令牌
 */

@Configuration
//@EnableAuthorizationServer
public class AuthorizationServerInMemoryConfig extends AuthorizationServerConfigurerAdapter {

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final JwtAccessTokenConverter jwtAccessTokenConverter;

    public AuthorizationServerInMemoryConfig(PasswordEncoder passwordEncoder,
                                             AuthenticationManager authenticationManager,
                                             JwtAccessTokenConverter jwtAccessTokenConverter) {
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtAccessTokenConverter = jwtAccessTokenConverter;
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        // TODO: 2020/10/26 字段改成配置 
        clients.inMemory()
                .withClient("test-client")
                .secret(passwordEncoder.encode("test-client-secret"))
                .authorizedGrantTypes("authorization_code", "password")
                .scopes("test")
                .redirectUris("https://www.fmisser.com")
                .accessTokenValiditySeconds(72000)
                .refreshTokenValiditySeconds(72000);
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints
                .accessTokenConverter(jwtAccessTokenConverter)    // token transform to jwt
                .authenticationManager(authenticationManager);  // oauth2 password need

    }
}
