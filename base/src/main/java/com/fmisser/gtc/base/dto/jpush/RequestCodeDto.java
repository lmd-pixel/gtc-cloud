package com.fmisser.gtc.base.dto.jpush;

import lombok.Data;

/**
 * 请求验证码
 */
@Data
public class RequestCodeDto {

    public RequestCodeDto(String mobile, int temp_id) {
        this.mobile = mobile;
        this.temp_id = temp_id;
    }

    private String mobile;
    private int temp_id;
}
