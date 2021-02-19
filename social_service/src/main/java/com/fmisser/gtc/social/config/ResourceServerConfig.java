package com.fmisser.gtc.social.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

    private final AuthenticationEntryPoint authenticationEntryPoint;

    private final AccessDeniedHandler accessDeniedHandler;

    private final CommonAccessDecisionManager commonAccessDecisionManager;

    public ResourceServerConfig(@Qualifier("auth_ex_handler") AuthenticationEntryPoint authenticationEntryPoint,
                                @Qualifier("access_denied_handler") AccessDeniedHandler accessDeniedHandler,
                                CommonAccessDecisionManager commonAccessDecisionManager) {
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.accessDeniedHandler = accessDeniedHandler;
        this.commonAccessDecisionManager = commonAccessDecisionManager;
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests()
                // 自定义鉴权
//                .accessDecisionManager(commonAccessDecisionManager)
                .antMatchers("/test/**").permitAll()
                .antMatchers("/dynamic/**").permitAll()
                .antMatchers("/comm/**").permitAll()
                .antMatchers("/im_cb/**").permitAll()
                .antMatchers("/recommend/**").permitAll()
//                swagger
                .antMatchers("/swagger-ui.html").permitAll()
                .antMatchers("/webjars/**").permitAll()
                .antMatchers("/swagger-resources/**").permitAll()
                .antMatchers("/swagger/**").permitAll()
                .antMatchers("/v2/api-docs/**").permitAll()
//                eureka
                .antMatchers("/actuator/**").permitAll()
                .anyRequest().authenticated();
    }

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        // 自定义认证异常返回数据
        resources.authenticationEntryPoint(authenticationEntryPoint)
                .accessDeniedHandler(accessDeniedHandler);
    }
}
