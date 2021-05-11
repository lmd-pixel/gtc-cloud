package com.fmisser.fpp.thirdparty.jpush.dto;

import lombok.Data;

/**
 * @author fmisser
 * @create 2021-05-11 下午2:34
 * @description
 */
@Data
public class PhoneCodeGetResp {
    private String msg_id;
    private PhoneCodeGetError error;
}
