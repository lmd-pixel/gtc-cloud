package com.fmisser.gtc.base.utils;

import lombok.SneakyThrows;
import org.bouncycastle.util.encoders.Base64;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CryptoUtilsTest {

    @SneakyThrows
    @Test
    void aesEncrypt() {
        String content = "hello,world!";
        String key = "test_key&123456";

        byte[] encryptString = CryptoUtils.aesEncrypt(content.getBytes(), key, false);
        byte[] originString = CryptoUtils.aesEncrypt(encryptString, key, true);

        assertEquals(content, new String(originString));

        String encryptEncodeString = new String(Base64.encode(encryptString));
        System.out.println(encryptEncodeString);

        originString = CryptoUtils.aesEncrypt(Base64.decode(encryptEncodeString.getBytes()), key, true);

        assertEquals(content, new String(originString));
    }

    @Test
    void base64() {
        String content = "hello,world!";

        String encodeString = CryptoUtils.base64Encode(content.getBytes());
        System.out.println(encodeString);

        byte[] bytes = CryptoUtils.base64Decode(encodeString);

        assertEquals(content, new String(bytes));
    }

    @SneakyThrows
    @Test
    void base64aes() {
        String content = "你好，世界!";
        String key = "test**&**test";

        String secretString = CryptoUtils.base64AesSecret(content, key);
        String originString = CryptoUtils.base64AesOrigin(secretString, key);

        System.out.println(secretString);
        System.out.println(originString);

        Assertions.assertEquals(content, originString);
    }
}