package com.fmisser.gtc.base.exception.oauth2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fmisser.gtc.base.exception.ApiErrorEnum;
import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.base.i18n.LocaleMessageUtil;
import com.fmisser.gtc.base.response.ApiResp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 自定义认证失败异常返回结构
 */

@Component("auth_ex_handler")
public class CustomAuthExceptionHandler implements AuthenticationEntryPoint {

    private final LocaleMessageUtil localeMessageUtil;

    private final ObjectMapper objectMapper;

    public CustomAuthExceptionHandler(LocaleMessageUtil localeMessageUtil,
                                      ObjectMapper objectMapper) {
        this.localeMessageUtil = localeMessageUtil;
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        ApiException exception = new ApiException(ApiErrorEnum.BAD_CREDENTIALS.getCode(),
                localeMessageUtil.getLocaleErrorMessage(ApiErrorEnum.BAD_CREDENTIALS));

        response.setContentType("application/json;charset=utf-8");
        response.getWriter().print(objectMapper.writeValueAsString(ApiResp.failed(exception)));
    }
}
