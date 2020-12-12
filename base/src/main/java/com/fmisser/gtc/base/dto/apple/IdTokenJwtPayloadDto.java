package com.fmisser.gtc.base.dto.apple;

import lombok.Data;

@Data
public class IdTokenJwtPayloadDto {
    private String iss;
    private String iat;
    private String exp;
    private String aud;
    private String sub;
}
