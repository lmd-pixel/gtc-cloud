package com.fmisser.fpp.thirdparty.apple.dto;

import lombok.Data;

import java.util.List;

/**
 * @author fmisser
 * @create 2021-05-11 下午3:32
 * @description 苹果登录授权公钥 https://appleid.apple.com/auth/keys
 */

@Data
public class AppleIdLoginAuthKeysResp {
    private List<AppleIdLoginAuthKeyItem> keys;
}
