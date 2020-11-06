package com.fmisser.gtc.base.utils;

import javax.crypto.Cipher;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

/**
 * 极光相关功能
 */

public class JPushUtils {

    /**
     * rsa 解密
     * http://docs.jiguang.cn/jverification/server/rest_api/loginTokenVerify_api/
     */
    public static String rsaDecrypt(String cryptograph, String prikey) throws Exception {
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(prikey));
        PrivateKey privateKey = KeyFactory.getInstance("RSA").generatePrivate(keySpec);

        Cipher cipher=Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);

        byte [] b = Base64.getDecoder().decode(cryptograph);
        return new String(cipher.doFinal(b));
    }

    /**
     * 生成auth string
     * http://docs.jiguang.cn/jverification/server/rest_api/rest_api_summary/
     */
    public static String genAuthString(String appKey, String masterSecret) {
        return AuthUtils.genBasicAuthString(appKey, masterSecret);
    }
}
