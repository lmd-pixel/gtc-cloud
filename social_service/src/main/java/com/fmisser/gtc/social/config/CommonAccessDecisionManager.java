package com.fmisser.gtc.social.config;

import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.base.exception.oauth2.ForbiddenAccountAccessDeniedException;
import com.fmisser.gtc.social.domain.Forbidden;
import com.fmisser.gtc.social.domain.User;
import com.fmisser.gtc.social.service.ForbiddenService;
import com.fmisser.gtc.social.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Iterator;

@Configuration
public class CommonAccessDecisionManager implements AccessDecisionManager {

    @Autowired
    private ForbiddenService forbiddenService;

    @Autowired
    private UserService userService;

    @Override
    public void decide(Authentication authentication, Object object, Collection<ConfigAttribute> configAttributes) throws AccessDeniedException, InsufficientAuthenticationException, ApiException {

        String username = authentication.getPrincipal().toString();
        // 未授权默认登录的账户名字未 anonymousUser
        if (username.equals("anonymousUser")) {
            return;
        }

        User user = userService.getUserByUsername(username);
        Forbidden forbidden = forbiddenService.getUserForbidden(user);
        if (forbidden != null) {
            if (forbidden.getDays() == 0) {
                throw new ForbiddenAccountAccessDeniedException("当前账号存在严重违规行为，已被系统永久禁封");
            } else {
                SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("MM月dd日HH:mm");
                String msg = String.format("当前账号存在违规行为，已被系统封禁%d天，将于%s解封",
                        forbidden.getDays(), dateTimeFormatter.format(forbidden.getEndTime()));
                throw new ForbiddenAccountAccessDeniedException(msg);
            }
        }
    }

    @Override
    public boolean supports(ConfigAttribute attribute) {
        return true;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }
}
