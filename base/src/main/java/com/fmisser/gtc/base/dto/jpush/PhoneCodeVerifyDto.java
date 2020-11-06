package com.fmisser.gtc.base.dto.jpush;

import lombok.Data;

@Data
public class PhoneCodeVerifyDto {
    private boolean is_valid;

    private PhoneCodeDto.PhoneCodeError error;
}
