package com.fmisser.gtc.auth.config;

import com.fmisser.gtc.auth.config.autologin.AutoLoginTokenGranter;
import com.fmisser.gtc.auth.config.smslogin.SmsLoginTokenGranter;
import com.fmisser.gtc.auth.config.thirdpartlogin.AppleLoginTokenGranter;
import com.fmisser.gtc.auth.service.impl.UserDetailServiceImpl;
import com.fmisser.gtc.base.exception.oauth2.CustomExceptionTranslator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.CompositeTokenGranter;
import org.springframework.security.oauth2.provider.OAuth2RequestFactory;
import org.springframework.security.oauth2.provider.TokenGranter;
import org.springframework.security.oauth2.provider.client.ClientCredentialsTokenGranter;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeTokenGranter;
import org.springframework.security.oauth2.provider.implicit.ImplicitTokenGranter;
import org.springframework.security.oauth2.provider.password.ResourceOwnerPasswordTokenGranter;
import org.springframework.security.oauth2.provider.refresh.RefreshTokenGranter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

/**
 * 基于JDBC存储token
 */

@Configuration
@EnableAuthorizationServer
public class AuthorizationServerWithJDBCConfig extends AuthorizationServerConfigurerAdapter {

//    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final DataSource dataSource;

    private final JwtAccessTokenConverter jwtAccessTokenConverter;

    private final UserDetailsService userDetailsService;

    private final CustomExceptionTranslator customExceptionTranslator;

    public AuthorizationServerWithJDBCConfig(DataSource dataSource,
//                                             PasswordEncoder passwordEncoder,
                                             AuthenticationManager authenticationManager,
                                             JwtAccessTokenConverter jwtAccessTokenConverter,
                                             @Qualifier("top") UserDetailsService userDetailsService,
                                             @Qualifier("exception_translator") CustomExceptionTranslator customExceptionTranslator) {
        this.dataSource = dataSource;
//        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtAccessTokenConverter = jwtAccessTokenConverter;
        this.userDetailsService = userDetailsService;
        this.customExceptionTranslator = customExceptionTranslator;
    }

    @Bean
    public TokenStore tokenStore() {
        return new JdbcTokenStore(dataSource);
    }

    @Bean
    public CommonTokenService commonTokenService() {
        CommonTokenService commonTokenService = new CommonTokenService();
        commonTokenService.setTokenStore(tokenStore());
        commonTokenService.setSupportRefreshToken(true);
        commonTokenService.setTokenEnhancer(jwtAccessTokenConverter);
        return commonTokenService;
    }

    @Bean
    public ClientDetailsService clientDetailsService() {
        return new JdbcClientDetailsService(dataSource);
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
//        JDBC模式手动在表：oauth_client_details 中填入授权等信息
//        System.out.println("oauth 2 secret encode:" + this.passwordEncoder.encode("test-client-secret"));
        clients.withClientDetails(clientDetailsService());
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        // 重设认证,增加短信验证和手机号一键登录类型
        endpoints.tokenGranter(new CompositeTokenGranter(
                this.getTokenGranters(
                        endpoints.getClientDetailsService(),
                        endpoints.getTokenServices(),
                        endpoints.getAuthorizationCodeServices(),
                        endpoints.getOAuth2RequestFactory())));

        endpoints
                .tokenStore(tokenStore())
                .accessTokenConverter(jwtAccessTokenConverter)
                .userDetailsService(userDetailsService)
                .authenticationManager(authenticationManager)
                .tokenServices(commonTokenService()); // 自定义token service

        // 自定义异常返回
        // controller封装了一层oauth/token的调用，不用再处理自定义异常
//        endpoints.exceptionTranslator(customExceptionTranslator);
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security.allowFormAuthenticationForClients()
                .tokenKeyAccess("permitAll()")
                .checkTokenAccess("isAuthenticated()");
    }

    /**
     * 添加自定义的短信验证类型
     * 原来的参考 {@link AuthorizationServerEndpointsConfigurer#getDefaultTokenGranters()}
     */
    private List<TokenGranter> getTokenGranters(ClientDetailsService clientDetailsService,
                                                AuthorizationServerTokenServices tokenServices,
                                                AuthorizationCodeServices authorizationCodeServices,
                                                OAuth2RequestFactory requestFactory) {
        List<TokenGranter> tokenGranters = new ArrayList<>();
        tokenGranters.add(new AuthorizationCodeTokenGranter(tokenServices,
                authorizationCodeServices, clientDetailsService,requestFactory));
        tokenGranters.add(new RefreshTokenGranter(tokenServices,
                clientDetailsService, requestFactory));
        tokenGranters.add(new ImplicitTokenGranter(tokenServices,
                clientDetailsService, requestFactory));
        tokenGranters.add(new ClientCredentialsTokenGranter(tokenServices,
                clientDetailsService, requestFactory));
        if (authenticationManager != null) {
            tokenGranters.add(new ResourceOwnerPasswordTokenGranter(authenticationManager,
                    tokenServices, clientDetailsService, requestFactory));
        }
        // 新增短信验证
        tokenGranters.add(new SmsLoginTokenGranter(tokenServices,
                clientDetailsService,requestFactory, (UserDetailServiceImpl) userDetailsService));
        // 新增手机号一键登录验证
        tokenGranters.add(new AutoLoginTokenGranter(tokenServices,
                clientDetailsService,requestFactory, (UserDetailServiceImpl) userDetailsService));
        // 新增苹果一键登录
        tokenGranters.add(new AppleLoginTokenGranter(tokenServices,
                clientDetailsService,requestFactory, (UserDetailServiceImpl) userDetailsService));

        return tokenGranters;
    }
}
