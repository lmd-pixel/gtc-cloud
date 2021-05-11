package com.fmisser.fpp.thirdparty.apple.dto;

import lombok.Data;

/**
 * @author fmisser
 * @create 2021-05-11 下午3:33
 * @description
 */

@Data
public class AppleIdLoginAuthKeyItem {
    private String kty;
    private String kid;
    private String use;
    private String alg;
    private String n;
    private String e;
}
