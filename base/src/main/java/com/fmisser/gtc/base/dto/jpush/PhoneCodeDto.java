package com.fmisser.gtc.base.dto.jpush;

import lombok.Data;

/**
 * 请求验证码返回
 */
@Data
public class PhoneCodeDto {

    @Data
    public class PhoneCodeError {
        private int code;
        private String message;
    }

    private String msg_id;
    private PhoneCodeError error;
}
