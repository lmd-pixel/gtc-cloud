package com.fmisser.fpp.thirdparty.jpush.dto;

/**
 * @author fmisser
 * @create 2021-05-11 下午2:35
 * @description
 */

// json的序列化或者反序列化通过 get set函数，
// lombok生成的boolean型属性的get函数和int等类型不同,
// 所以这里自己写 get set 函数
//@Data
public class PhoneCodeVerifyResp {
    private boolean is_valid;
    private PhoneCodeGetError error;

    public boolean getIs_valid() {
        return is_valid;
    }

    public void setIs_valid(boolean is_valid) {
        this.is_valid = is_valid;
    }

    public PhoneCodeGetError getError() {
        return error;
    }

    public void setError(PhoneCodeGetError error) {
        this.error = error;
    }
}
