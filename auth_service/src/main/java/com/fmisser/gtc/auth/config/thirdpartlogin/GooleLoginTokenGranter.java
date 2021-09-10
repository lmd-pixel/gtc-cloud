package com.fmisser.gtc.auth.config.thirdpartlogin;

import com.fmisser.gtc.auth.service.impl.UserDetailServiceImpl;
import lombok.SneakyThrows;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.token.AbstractTokenGranter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Map;

public class GooleLoginTokenGranter extends AbstractTokenGranter {
    private static final String GRANT_TYPE = "goole_login";

    private final UserDetailServiceImpl userDetailsService;
    private final OAuth2RequestFactory oAuth2RequestFactory;



    public GooleLoginTokenGranter(AuthorizationServerTokenServices tokenServices,
                               ClientDetailsService clientDetailsService,
                               OAuth2RequestFactory requestFactory,
                               UserDetailServiceImpl userDetailsService) {
        super(tokenServices, clientDetailsService, requestFactory, GRANT_TYPE);

        this.userDetailsService = userDetailsService;
        this.oAuth2RequestFactory = requestFactory;
    }


    @SneakyThrows
    @Override
    protected OAuth2Authentication getOAuth2Authentication(ClientDetails client, TokenRequest tokenRequest) {
        Map<String, String> parameters = tokenRequest.getRequestParameters();
        UserDetails userDetails = getUser(parameters);
        if (userDetails == null) {
            // TODO: 2020/10/26 改成配置
            throw new InvalidGrantException("无法获取用户信息");
        }

        OAuth2Request oAuth2Request = oAuth2RequestFactory.createOAuth2Request(client, tokenRequest);
        PreAuthenticatedAuthenticationToken authenticatedAuthenticationToken =
                new PreAuthenticatedAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        authenticatedAuthenticationToken.setDetails(userDetails);
        return new OAuth2Authentication(oAuth2Request, authenticatedAuthenticationToken);
    }

    private UserDetails getUser(Map<String, String> params) throws GeneralSecurityException, IOException {
        // 获取 openid 字段
        // TODO: 2020/10/26 改成配置
        return userDetailsService.loaduserByGooleLogin(params.get("code"),params.get("token"));
    }

}
