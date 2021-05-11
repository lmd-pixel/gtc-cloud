package com.fmisser.fpp.thirdparty.jpush.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author fmisser
 * @create 2021-05-11 下午2:21
 * @description
 */

@Data
@AllArgsConstructor
public class AutoLoginVerifyReq {
    private String loginToken;
    private String exID;
}
