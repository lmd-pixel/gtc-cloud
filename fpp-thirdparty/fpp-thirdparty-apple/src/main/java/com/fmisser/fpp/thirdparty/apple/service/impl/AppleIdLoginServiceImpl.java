package com.fmisser.fpp.thirdparty.apple.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fmisser.fpp.thirdparty.apple.dto.AppleIdLoginAuthKeyItem;
import com.fmisser.fpp.thirdparty.apple.dto.AppleIdLoginAuthKeysResp;
import com.fmisser.fpp.thirdparty.apple.dto.AppleIdLoginIdentityTokenHeader;
import com.fmisser.fpp.thirdparty.apple.feign.AppleIdLoginGetAuthKeysFeign;
import com.fmisser.fpp.thirdparty.apple.service.AppleIdLoginService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Date;
import java.util.Optional;

/**
 * @author fmisser
 * @create 2021-05-14 下午8:25
 * @description
 */
@Slf4j
@Service
@AllArgsConstructor
public class AppleIdLoginServiceImpl implements AppleIdLoginService {
    private static final String AUTH_ISSUER_URL = "https://appleid.apple.com";

    private final ObjectMapper objectMapper;
    private final AppleIdLoginGetAuthKeysFeign appleIdLoginGetAuthKeysFeign;

    @Override
    public boolean verifyIdentityToken(String audience, String subject, String identityToken, boolean strict) throws RuntimeException {
        log.info("[apple] verify identityToken: {} with audience: {} and subject: {} use strict mode: {}",
                identityToken, audience, subject, strict);

        String[] jwtContentList = identityToken.split("\\.");
        if (jwtContentList.length != 3) {
            log.error("[apple] verify identityToken failed: not a valid jwt format");
            throw new RuntimeException("验证失败");
        }

        // 获取jwt header 里解密需要的kid
        String headerString = new String(Base64Utils.decodeFromString(jwtContentList[0]));
        AppleIdLoginIdentityTokenHeader tokenHeader;

        try {
            tokenHeader = objectMapper.readValue(headerString, AppleIdLoginIdentityTokenHeader.class);
        } catch (JsonProcessingException e) {
            log.error("[apple] verify identityToken failed: not a valid jwt header");
            throw new RuntimeException("验证失败");
        }

        String kty, n, e;
        AppleIdLoginAuthKeysResp authKeys;

        try {
            // 从苹果获取当前public keys
            authKeys = appleIdLoginGetAuthKeysFeign.getAuthKeys();
        } catch (Exception e1) {
            log.error("[apple] verify identityToken failed, try to get public keys from apple exception: {}",
                    e1.getMessage());

            // 如果使用严格模式则报错，否则认为成功
            if (strict) {
                log.error("[apple] verify identityToken with strict mode failed.");
                throw new RuntimeException("验证失败");
            } else {
                log.info("[apple] verify identityToken with no strict mode success.");
                return true;
            }
        }

        // 通过 jwt header kid 找到对应的公钥数据
        Optional<AppleIdLoginAuthKeyItem> keyItem = authKeys
                .getKeys()
                .stream()
                .filter( key -> key.getKid().equals(tokenHeader.getKid()))
                .findFirst();

        if (!keyItem.isPresent()) {
            log.error("[apple] verify identityToken cannot get available auth key.");
            throw new RuntimeException("验证失败");
        }

        kty = keyItem.get().getKty();
        n = keyItem.get().getN();
        e = keyItem.get().getE();

        // 创建公钥
        BigInteger modules =
                new BigInteger(1, Base64Utils.decodeFromUrlSafeString(n));
        BigInteger publicExponent =
                new BigInteger(1, Base64Utils.decodeFromUrlSafeString(e));
        RSAPublicKeySpec rsaPublicKeySpec = new RSAPublicKeySpec(modules, publicExponent);

        KeyFactory keyFactory;
        try {
            keyFactory = KeyFactory.getInstance(kty);
        } catch (NoSuchAlgorithmException ex) {
            log.error("[apple] verify identityToken cannot get keyFactory with algorithm: {}.", kty);
            throw new RuntimeException("验证失败");
        }

        PublicKey publicKey;
        try {
            publicKey = keyFactory.generatePublic(rsaPublicKeySpec);
        } catch (InvalidKeySpecException ex) {
            log.error("[apple] verify identityToken cannot get publicKey.");
            throw new RuntimeException("验证失败");
        }

        // 创建jwt验证解析器
        JwtParser jwtParser;
        if (strict) {
            jwtParser = Jwts.parser()
                    .requireIssuer(AUTH_ISSUER_URL)
                    .requireAudience(audience)
                    .requireSubject(subject)
                    .setSigningKey(publicKey);
        } else {
            jwtParser = Jwts.parser()
                    .requireIssuer(AUTH_ISSUER_URL)
                    .setSigningKey(publicKey);
        }

        // 验证 jwt
        Jws<Claims> claimsJws = jwtParser.parseClaimsJws(identityToken);
        if (claimsJws == null || claimsJws.getBody() == null) {
            log.error("[apple] verify identityToken verify failed: result is null.");
            throw new RuntimeException("验证失败");
        } else if (claimsJws.getBody().getExpiration() == null ||
                claimsJws.getBody().getExpiration().before(new Date())) {
            log.error("[apple] verify identityToken verify failed: expiration is: {}.",
                    claimsJws.getBody().getExpiration());
            throw new RuntimeException("验证失败");
        }

        return true;
    }
}
