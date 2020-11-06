package com.fmisser.gtc.base.utils;

import java.util.Base64;

/**
 * oauth 相关
 */
public class AuthUtils {

    /**
     * 生成basic auth 字符串
     */
    public static String genBasicAuthString(String client, String client_secret) {
        String base64String = Base64.getEncoder()
                .encodeToString(String.format("%s:%s", client, client_secret).getBytes());
        return String.format("Basic %s", base64String);
    }
}
