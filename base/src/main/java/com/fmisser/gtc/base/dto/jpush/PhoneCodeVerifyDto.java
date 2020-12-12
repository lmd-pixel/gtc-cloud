package com.fmisser.gtc.base.dto.jpush;

import lombok.Data;

//@Data
public class PhoneCodeVerifyDto {
    private boolean is_valid;

    private PhoneCodeDto.PhoneCodeError error;

    public boolean getIs_valid() {
        return is_valid;
    }

    public void setIs_valid(boolean is_valid) {
        this.is_valid = is_valid;
    }
}
