package com.fmisser.gtc.base.utils;

import org.bouncycastle.util.encoders.Base64;
import org.springframework.util.Base64Utils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.SecureRandom;

/**
 * 加密解密
 */

public class CryptoUtils {

    /**
     * aes 加密解密
     */
    public static byte[] aesEncrypt(byte[] content, String key, boolean decrypt) throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");

        SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
        secureRandom.setSeed(key.getBytes());
        keyGenerator.init(128, secureRandom);

        SecretKey secretKey = keyGenerator.generateKey();
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        if (decrypt) {
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
        } else {
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        }
        return cipher.doFinal(content);
    }

    // base64 encode
    public static String base64Encode(byte[] content) {
//        return new String(Base64.encode(content));
        return Base64Utils.encodeToUrlSafeString(content);
    }

    // base64 decode
    public static byte[] base64Decode(String content) {
//        return Base64.decode(content.getBytes());
        return Base64Utils.decodeFromUrlSafeString(content);
    }

    public static String base64EncodeOrigin(String content) {
        return Base64Utils.encodeToString(content.getBytes());
    }

    public static String base64DecodeOrigin(String content) {
        return new String(Base64Utils.decodeFromString(content));
    }

    public static byte[] base64DecodeOriginToByteArray(String content) {
        return Base64Utils.decodeFromUrlSafeString(content);
    }

    // aes 加密 再 base64 encode
    public static String base64AesSecret(String content, String key) throws Exception {
        return base64Encode(aesEncrypt(content.getBytes(), key, false));
    }

    // base64 decode 再 aes 解密
    public static String base64AesOrigin(String content, String key) throws Exception {
        return new String(aesEncrypt(base64Decode(content), key, true));
    }
}
