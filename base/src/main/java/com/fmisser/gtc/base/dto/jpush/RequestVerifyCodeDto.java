package com.fmisser.gtc.base.dto.jpush;

import lombok.Data;

/**
 * 短信验证请求
 */
@Data
public class RequestVerifyCodeDto {
    public RequestVerifyCodeDto(String code) {
        this.code = code;
    }
    private String code;
}
