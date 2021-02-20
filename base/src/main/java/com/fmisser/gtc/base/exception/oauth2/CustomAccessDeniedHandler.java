package com.fmisser.gtc.base.exception.oauth2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fmisser.gtc.base.exception.ApiErrorEnum;
import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.base.i18n.LocaleMessageUtil;
import com.fmisser.gtc.base.response.ApiResp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 自定义访问异常返回结构
 * 权限不足等
 */
@Component("access_denied_handler")
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final LocaleMessageUtil localeMessageUtil;

    private final ObjectMapper objectMapper;

    public CustomAccessDeniedHandler(LocaleMessageUtil localeMessageUtil,
                                     ObjectMapper objectMapper) {
        this.localeMessageUtil = localeMessageUtil;
        this.objectMapper = objectMapper;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {

        ApiException exception;

        if (accessDeniedException instanceof ForbiddenAccountAccessDeniedException) {
            // 账号封禁
            exception = new ApiException(ApiErrorEnum.FORBIDDEN_ACCOUNT.getCode(),
                    accessDeniedException.getMessage());
        } else {
            // 通用访问失败
            exception = new ApiException(ApiErrorEnum.ACCESS_DENIED.getCode(),
                    localeMessageUtil.getLocaleErrorMessage(ApiErrorEnum.ACCESS_DENIED));
        }

        response.setContentType("application/json;charset=utf-8");
        response.getWriter().print(objectMapper.writeValueAsString(ApiResp.failed(exception)));
    }
}
