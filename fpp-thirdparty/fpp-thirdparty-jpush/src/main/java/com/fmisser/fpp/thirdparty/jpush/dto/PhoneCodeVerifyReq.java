package com.fmisser.fpp.thirdparty.jpush.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author fmisser
 * @create 2021-05-11 下午2:34
 * @description
 */
@Data
@AllArgsConstructor
public class PhoneCodeVerifyReq {
    private String code;
}
