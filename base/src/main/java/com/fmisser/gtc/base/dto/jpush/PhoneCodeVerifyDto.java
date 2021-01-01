package com.fmisser.gtc.base.dto.jpush;

import lombok.Data;

// json的序列化或者反序列化通过 get set函数， lombok生成的boolean型属性的get函数和int等类型不同
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
