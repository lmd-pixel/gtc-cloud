package com.fmisser.gtc.base.dto.apple;

import lombok.Data;

@Data
public class IdTokenJwtHeaderDto {
    private String alg;
    private String kid;
}
