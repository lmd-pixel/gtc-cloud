package com.fmisser.gtc.auth.config.autologin;

import com.fmisser.gtc.auth.service.impl.UserDetailServiceImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.token.AbstractTokenGranter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import java.util.Map;

/**
 * 手机号一键登录
 */

public class AutoLoginTokenGranter extends AbstractTokenGranter {
    // TODO: 2020/12/8 使用配置
    private static final String GRANT_TYPE = "auto_login";

    private final UserDetailServiceImpl userDetailsService;
    private final OAuth2RequestFactory oAuth2RequestFactory;

    public AutoLoginTokenGranter(AuthorizationServerTokenServices tokenServices,
                                    ClientDetailsService clientDetailsService,
                                    OAuth2RequestFactory requestFactory,
                                    UserDetailServiceImpl userDetailsService) {
        super(tokenServices, clientDetailsService, requestFactory, GRANT_TYPE);
        this.userDetailsService = userDetailsService;
        this.oAuth2RequestFactory = requestFactory;
    }

    @Override
    protected OAuth2Authentication getOAuth2Authentication(ClientDetails client, TokenRequest tokenRequest) {
        Map<String, String> parameters = tokenRequest.getRequestParameters();
        UserDetails userDetails = getUser(parameters);
        if (userDetails == null) {
            throw new InvalidGrantException("无法获取用户信息");
        }

        OAuth2Request oAuth2Request = oAuth2RequestFactory.createOAuth2Request(client, tokenRequest);
        PreAuthenticatedAuthenticationToken authenticatedAuthenticationToken =
                new PreAuthenticatedAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        authenticatedAuthenticationToken.setDetails(userDetails);
        return new OAuth2Authentication(oAuth2Request, authenticatedAuthenticationToken);
    }

    private UserDetails getUser(Map<String, String> params) {
        // 获取 phone 和 token 字段
        // TODO: 2020/12/8 使用配置
        return userDetailsService.loadUserByPhoneAuto(params.get("phone"), params.get("token"));
    }
}
