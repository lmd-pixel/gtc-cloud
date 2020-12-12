package com.fmisser.gtc.auth.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fmisser.gtc.auth.domain.AppleAuthToken;
import com.fmisser.gtc.auth.feign.AppleAuthFeign;
import com.fmisser.gtc.auth.repository.AppleAuthTokenRepository;
import com.fmisser.gtc.auth.service.ThirdPartyLoginService;
import com.fmisser.gtc.base.dto.apple.IdTokenJwtHeaderDto;
import com.fmisser.gtc.base.dto.apple.PublicKeysDto;
import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.base.prop.AppleConfProp;
import com.fmisser.gtc.base.utils.CryptoUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.net.URI;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.Date;
import java.util.Optional;

@Service
public class ThirdPartyLoginServiceImpl implements ThirdPartyLoginService {

    private final ObjectMapper objectMapper;

    private final AppleAuthFeign appleAuthFeign;

    private final AppleConfProp appleConfProp;

    private final AppleAuthTokenRepository appleAuthTokenRepository;

    public ThirdPartyLoginServiceImpl(ObjectMapper objectMapper,
                                      AppleAuthFeign appleAuthFeign,
                                      AppleConfProp appleConfProp,
                                      AppleAuthTokenRepository appleAuthTokenRepository) {
        this.objectMapper = objectMapper;
        this.appleAuthFeign = appleAuthFeign;
        this.appleConfProp = appleConfProp;
        this.appleAuthTokenRepository = appleAuthTokenRepository;
    }

    @Override
    @SneakyThrows
    public boolean checkAppleIdentityToken(String identityToken, String subject) throws ApiException {

        // save to db
        AppleAuthToken appleAuthToken = new AppleAuthToken();
        appleAuthToken.setSubject(subject);
        appleAuthToken.setIdentityToken(identityToken);
        appleAuthToken = appleAuthTokenRepository.save(appleAuthToken);

        String[] jwtContentList = identityToken.split(".");
        if (jwtContentList.length != 2) {
            throw new ApiException(-1, "非法数据，无法验证!");
        }

        // 获取jwt header 里解密需要的kid
        String headerString = CryptoUtils.base64DecodeOrigin(jwtContentList[0]);
        IdTokenJwtHeaderDto idTokenJwtHeaderDto = objectMapper.readValue(headerString, IdTokenJwtHeaderDto.class);

        // 从苹果获取当前public keys
        PublicKeysDto publicKeysDto = appleAuthFeign.getAuthKeys(new URI(appleConfProp.getAuthKeysUrl()));

        // 通过 jwt header kid 找到对应的公钥数据
        Optional<PublicKeysDto.KeyItem> keyItem = publicKeysDto
                .getKeys()
                .stream()
                .filter( key -> key.getKid().equals(idTokenJwtHeaderDto.getKid()))
                .findFirst();

        if (!keyItem.isPresent()) {
            throw new ApiException(-1, "未找到公钥数据!");
        }

        // 创建公钥
        BigInteger modules =
                new BigInteger(1, CryptoUtils.base64DecodeOriginToByteArray(keyItem.get().getN()));
        BigInteger publicExponent =
                new BigInteger(1, CryptoUtils.base64DecodeOriginToByteArray(keyItem.get().getE()));
        RSAPublicKeySpec rsaPublicKeySpec = new RSAPublicKeySpec(modules, publicExponent);
        KeyFactory keyFactory = KeyFactory.getInstance(keyItem.get().getKty());
        PublicKey publicKey = keyFactory.generatePublic(rsaPublicKeySpec);

        // 验证token
        JwtParser jwtParser = Jwts.parser()
                .requireIssuer(appleConfProp.getAuthIssuerUrl())
                .requireAudience(appleConfProp.getAuthAudience())
                .requireSubject(subject)
                .setSigningKey(publicKey);

        Jws<Claims> claimsJws = jwtParser.parseClaimsJws(identityToken);
        if (claimsJws == null ||
                claimsJws.getBody().getExpiration() == null ||
                claimsJws.getBody().getExpiration().before(new Date())) {
            throw new ApiException(-1, "token解析出错或已超时");
        }

        // succeed
        appleAuthToken.setSucceed(1);
        appleAuthTokenRepository.save(appleAuthToken);

        return true;
    }
}
