package com.fmisser.fpp.thirdparty.jpush.dto;

import lombok.Data;

/**
 * @author fmisser
 * @create 2021-05-11 下午2:21
 * @description
 */

@Data
public class AutoLoginVerifyResp {
    private Long id;
    private int code;
    private String exID;
    private String content;
    private String phone;
}
