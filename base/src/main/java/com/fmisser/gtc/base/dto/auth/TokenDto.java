package com.fmisser.gtc.base.dto.auth;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

/**
 * oauth2 返回的token结构
 */

@Data
public class TokenDto {

    private String access_token;

    @JsonIgnore
    private String token_type;

    private String refresh_token;

    @JsonIgnore
    private int expires_in;

    @JsonIgnore
    private String scope;

}
