package com.fmisser.gtc.base.exception.oauth2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fmisser.gtc.base.exception.ApiErrorEnum;
import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.base.i18n.LocaleMessageUtil;
import com.fmisser.gtc.base.response.ApiResp;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.stereotype.Component;

@Component("exception_translator")
public class CustomExceptionTranslator implements WebResponseExceptionTranslator<OAuth2Exception> {

    private final LocaleMessageUtil localeMessageUtil;

    public CustomExceptionTranslator(LocaleMessageUtil localeMessageUtil) {
        this.localeMessageUtil = localeMessageUtil;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public ResponseEntity<OAuth2Exception> translate(Exception e) throws Exception {
        ApiException exception = new ApiException(ApiErrorEnum.UNAUTHORIZED.getCode(),
                        localeMessageUtil.getLocaleErrorMessage(ApiErrorEnum.UNAUTHORIZED));
        ApiResp<Object> resp = ApiResp.failed(exception);

        // 从 ApiResp 构造 OAuth2Exception，因为我们本来要统一返回 ApiResp， 但是这里返回类型为 OAuth2Exception
//        OAuth2Exception body = OAuth2Exception.create("", "");
//        body.addAdditionalInformation("code", String.valueOf(resp.getCode()));
//        body.addAdditionalInformation("success", String.valueOf(resp.isSuccess()));
//        body.addAdditionalInformation("message", resp.getMessage());
//        body.addAdditionalInformation("data", null);

        // 这里我们不转化，直接返回ApiResp
        return new ResponseEntity(resp, HttpStatus.OK);
    }
}
