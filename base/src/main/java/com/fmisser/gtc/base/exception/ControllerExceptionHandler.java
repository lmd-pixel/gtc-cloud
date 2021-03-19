package com.fmisser.gtc.base.exception;

import com.fmisser.gtc.base.i18n.LocaleMessageUtil;
import com.fmisser.gtc.base.response.ApiResp;
import com.fmisser.gtc.base.response.ApiRespHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;

/**
 * 全局异常处理
 */

@RestControllerAdvice
public class ControllerExceptionHandler {
    Logger logger = LoggerFactory.getLogger(ControllerExceptionHandler.class);

    private final ApiRespHelper apiRespHelper;

    public ControllerExceptionHandler(ApiRespHelper apiRespHelper) {
        this.apiRespHelper = apiRespHelper;
    }

    // api exception
    @ExceptionHandler(ApiException.class)
    public <T> ApiResp<T> handleApiException(HttpServletRequest request, ApiException e) {
        logger.error(request.getRequestURI() + " failed: " + e.getMessage());
        return ApiResp.failed(e);
    }

    // http exception

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public <T> ApiResp<T> handleHttpMessageNotReadableException(HttpServletRequest request, HttpMessageNotReadableException e) {
        return innerCreator(request, ApiErrorEnum.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public <T> ApiResp<T> handleHttpRequestMethodNotSupportedException(HttpServletRequest request, HttpRequestMethodNotSupportedException e) {
        return innerCreator(request, ApiErrorEnum.METHOD_NOT_ALLOWED);
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(AuthenticationException.class)
    public <T> ApiResp<T> handleAuthenticationException(HttpServletRequest request, AuthenticationException e) {
        return innerCreator(request, ApiErrorEnum.UNAUTHORIZED);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public <T> ApiResp<T> handleNoHandlerFoundException(HttpServletRequest request, NoHandlerFoundException e) {
        return innerCreator(request, ApiErrorEnum.NOT_FOUND);
    }

    // spring security exception

    @ExceptionHandler(AccessDeniedException.class)
    public <T> ApiResp<T> handleAccessDeniedException(HttpServletRequest request, AccessDeniedException e) {
        return innerCreator(request, ApiErrorEnum.ACCESS_DENIED);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public <T> ApiResp<T> handleBadCredentialsException(HttpServletRequest request, BadCredentialsException e) {
        return innerCreator(request, ApiErrorEnum.BAD_CREDENTIALS);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public <T> ApiResp<T> handleException(HttpServletRequest request, Exception e) {
        logger.error(e.getMessage());
//        return innerCreator(request, ApiErrorEnum.INTERNAL_SERVER_ERROR);
        return ApiResp.failed(-1, e.getMessage());
    }

    private <T> ApiResp<T> innerCreator(HttpServletRequest request, ApiErrorEnum error) {
        ApiResp<T> resp = apiRespHelper.error(error);
        logger.error(request.getRequestURI() + " failed: " + resp.getMessage());
        return resp;
    }
}
