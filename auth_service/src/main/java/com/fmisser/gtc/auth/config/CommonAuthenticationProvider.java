package com.fmisser.gtc.auth.config;

import com.fmisser.gtc.auth.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

/**
 * 自定义认证
 */
//@Configuration
//public class CommonAuthenticationProvider implements AuthenticationProvider {
//
//    final UserService userService;
//
//    public CommonAuthenticationProvider(UserService userService) {
//        this.userService = userService;
//    }
//
//    @Override
//    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
//        return new UsernamePasswordAuthenticationToken(authentication.getPrincipal(),
//                authentication.getCredentials(), authentication.getAuthorities());
//    }
//
//    @Override
//    public boolean supports(Class<?> authentication) {
////        return authentication.equals(UsernamePasswordAuthenticationToken.class);
//        return true;
//    }
//}
