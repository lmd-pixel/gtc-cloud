package com.fmisser.gtc.auth.config;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.token.Token;
import org.springframework.security.core.token.TokenService;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 自定义 token service
 */

public class CommonTokenService extends DefaultTokenServices {

    public CommonTokenService() {
        super();
    }

    @Transactional
    @Override
    public OAuth2AccessToken createAccessToken(OAuth2Authentication authentication) throws AuthenticationException {
        // 每次请求都删除旧的token
        OAuth2AccessToken existingAccessToken = getAccessToken(authentication);
        if (existingAccessToken != null) {
            revokeToken(existingAccessToken.getValue());
        }
        return super.createAccessToken(authentication);
    }
}
