package com.fmisser.gtc.base.i18n;

import com.fmisser.gtc.base.exception.ApiErrorEnum;
import com.fmisser.gtc.base.exception.ApiException;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class LocaleMessageUtil {
    private MessageSource messageSource;

    public LocaleMessageUtil(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public String getLocaleMessage(String code) {
        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage(code, null, locale);
    }

    public String getLocaleErrorMessage(ApiErrorEnum error) {
        return getLocaleMessage(error.getLocaleCode());
    }

    public ApiException getLocaleException(ApiErrorEnum error) {
        return new ApiException(error.getCode(), getLocaleMessage(error.getLocaleCode()));
    }
}
