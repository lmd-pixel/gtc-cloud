package com.fmisser.gtc.base.response;

import com.fmisser.gtc.base.exception.ApiErrorEnum;
import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.base.i18n.LocaleMessageUtil;
import org.springframework.stereotype.Component;

@Component
public class ApiRespHelper {
    private final LocaleMessageUtil localeMessageUtil;

    public ApiRespHelper(LocaleMessageUtil localeMessageUtil) {
        this.localeMessageUtil = localeMessageUtil;
    }

    public <T> ApiResp<T> error(ApiErrorEnum error) {
        ApiException apiException = this.localeMessageUtil.getLocaleException(error);
        return ApiResp.failed(apiException);
    }

    public <T> ApiResp<T> error() {
        ApiException apiException = this.localeMessageUtil.getLocaleException(ApiErrorEnum.UNKNOWN_ERROR);
        return ApiResp.failed(apiException);
    }
}
