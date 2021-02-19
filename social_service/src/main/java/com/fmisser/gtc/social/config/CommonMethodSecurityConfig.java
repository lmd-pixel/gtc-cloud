package com.fmisser.gtc.social.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;

//@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
//public class CommonMethodSecurityConfig extends GlobalMethodSecurityConfiguration {
//    private final CommonPermissionEvaluator commonAuthenticationProvider;
//
//    public CommonMethodSecurityConfig(CommonPermissionEvaluator commonAuthenticationProvider) {
//        this.commonAuthenticationProvider = commonAuthenticationProvider;
//    }
//
//    @Override
//    protected MethodSecurityExpressionHandler createExpressionHandler() {
//        DefaultMethodSecurityExpressionHandler expressionHandler =
//                new DefaultMethodSecurityExpressionHandler();
//        expressionHandler.setPermissionEvaluator(commonAuthenticationProvider);
//        return expressionHandler;
//    }
//}
