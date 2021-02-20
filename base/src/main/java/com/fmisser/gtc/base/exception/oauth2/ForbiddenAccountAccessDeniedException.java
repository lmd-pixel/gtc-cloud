package com.fmisser.gtc.base.exception.oauth2;

import org.springframework.security.access.AccessDeniedException;

/**
 * 账号被禁用异常
 */
public class ForbiddenAccountAccessDeniedException extends AccessDeniedException {

    public ForbiddenAccountAccessDeniedException(String msg) {
        super(msg);
    }

    public ForbiddenAccountAccessDeniedException(String msg, Throwable t) {
        super(msg, t);
    }
}
