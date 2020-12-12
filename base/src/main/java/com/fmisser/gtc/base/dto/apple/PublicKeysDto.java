package com.fmisser.gtc.base.dto.apple;

import lombok.Data;

import java.util.List;

/**
 * 苹果公钥结构
 * https://appleid.apple.com/auth/keys
 */

@Data
public class PublicKeysDto {
    private List<KeyItem> keys;

    @Data
    public static class KeyItem {
        private String kty;
        private String kid;
        private String use;
        private String alg;
        private String n;
        private String e;
    }
}
