package com.fmisser.gtc.base.dto.jpush;

import lombok.Data;

/**
 * 请求一键登录token的认证
 */
@Data
public class RequestLoginTokenVerifyDto {

    public RequestLoginTokenVerifyDto(String loginToken, String exID) {
        this.loginToken = loginToken;
        this.exID = exID;
    }

    private String loginToken;
    private String exID;
}
