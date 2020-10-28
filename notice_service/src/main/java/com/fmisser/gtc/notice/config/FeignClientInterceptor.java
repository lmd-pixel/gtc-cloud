package com.fmisser.gtc.notice.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.util.ObjectUtils;

public class FeignClientInterceptor implements RequestInterceptor {
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_TOKEN_TYPE = "Bearer";

    @Override
    public void apply(RequestTemplate requestTemplate) {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();

        if (!ObjectUtils.isEmpty(authentication) &&
                authentication.getDetails() instanceof OAuth2AuthenticationDetails) {
            OAuth2AuthenticationDetails details =
                    (OAuth2AuthenticationDetails) authentication.getDetails();

            // 传递token
            requestTemplate.header(AUTHORIZATION_HEADER,
                    String.format("%s %s", BEARER_TOKEN_TYPE, details.getTokenValue()));
        }
    }
}
