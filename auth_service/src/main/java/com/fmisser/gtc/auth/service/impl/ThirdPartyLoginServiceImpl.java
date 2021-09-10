package com.fmisser.gtc.auth.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fmisser.gtc.auth.domain.AppleAuthKey;
import com.fmisser.gtc.auth.domain.AppleAuthToken;
import com.fmisser.gtc.auth.feign.AppleAuthFeign;
import com.fmisser.gtc.auth.repository.AppleAuthKeyRepository;
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
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URI;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ThirdPartyLoginServiceImpl implements ThirdPartyLoginService {

    private final ObjectMapper objectMapper;

    private final AppleAuthFeign appleAuthFeign;

    private final AppleConfProp appleConfProp;

    private final AppleAuthTokenRepository appleAuthTokenRepository;

    private final AppleAuthKeyRepository appleAuthKeyRepository;


    public ThirdPartyLoginServiceImpl(ObjectMapper objectMapper,
                                      AppleAuthFeign appleAuthFeign,
                                      AppleConfProp appleConfProp,
                                      AppleAuthTokenRepository appleAuthTokenRepository,
                                      AppleAuthKeyRepository appleAuthKeyRepository) {
        this.objectMapper = objectMapper;
        this.appleAuthFeign = appleAuthFeign;
        this.appleConfProp = appleConfProp;
        this.appleAuthTokenRepository = appleAuthTokenRepository;
        this.appleAuthKeyRepository = appleAuthKeyRepository;
    }

    @Override
    @SneakyThrows(value = {
            JsonProcessingException.class,
//            JsonMappingException.class,
            NoSuchAlgorithmException.class,
            InvalidKeySpecException.class})
    public boolean checkAppleIdentityToken(String identityToken, String subject) throws ApiException {

        // save to db
        AppleAuthToken appleAuthToken = new AppleAuthToken();
        appleAuthToken.setSubject(subject);
        appleAuthToken.setIdentityToken(identityToken);
        appleAuthToken = appleAuthTokenRepository.save(appleAuthToken);

        String[] jwtContentList = identityToken.split("\\.");
        if (jwtContentList.length != 3) {
            throw new ApiException(-1, "非法数据，无法验证!");
        }

        // 获取jwt header 里解密需要的kid
        String headerString = CryptoUtils.base64DecodeOrigin(jwtContentList[0]);
        IdTokenJwtHeaderDto idTokenJwtHeaderDto = objectMapper.readValue(headerString, IdTokenJwtHeaderDto.class);

        String kty;
        String n;
        String e;

        try {
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

            kty = keyItem.get().getKty();
            n = keyItem.get().getN();
            e = keyItem.get().getE();

        } catch (Exception exception) {
            // 从本地去获取
            List<AppleAuthKey> appleAuthKeys = appleAuthKeyRepository.findAll();
            Optional<AppleAuthKey> appleAuthKey = appleAuthKeys.stream()
                    .filter( key -> key.getKid().equals(idTokenJwtHeaderDto.getKid()))
                    .findFirst();

            if (!appleAuthKey.isPresent()) {
                throw new ApiException(-1, "未找到公钥数据!");
            }

            kty = appleAuthKey.get().getKty();
            n = appleAuthKey.get().getN();
            e = appleAuthKey.get().getE();
        }

        // 创建公钥
        BigInteger modules =
                new BigInteger(1, CryptoUtils.base64DecodeOriginToByteArray(n));
        BigInteger publicExponent =
                new BigInteger(1, CryptoUtils.base64DecodeOriginToByteArray(e));
        RSAPublicKeySpec rsaPublicKeySpec = new RSAPublicKeySpec(modules, publicExponent);
        KeyFactory keyFactory = KeyFactory.getInstance(kty);
        PublicKey publicKey = keyFactory.generatePublic(rsaPublicKeySpec);

        // 验证token
        JwtParser jwtParser = Jwts.parser()
                .requireIssuer(appleConfProp.getAuthIssuerUrl())
//                不验证
//                .requireAudience(appleConfProp.getAuthAudience())
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

    @Override
    public boolean checkWxLogin(String unionid) throws ApiException {
        // 这里暂不验证
        return true;
    }

    @Override
    public boolean getGooleAccessTOken(String code,String token) throws ApiException, GeneralSecurityException, IOException {
/*
                 GoogleIdToken idToken = verifier.verify(token);
                  if (idToken == null) {
                    //  GoogleIdToken.Payload payload = idToken.getPayload();
                     *//* idToken.getHeader();
                      // Print user identifier
                      String userId = payload.getSubject();
                     System.out.println("User ID: " + userId);

                      String email = payload.getEmail();
                      boolean emailVerified = Boolean.valueOf(payload.getEmailVerified());
                     String name = (String) payload.get("name");
                      String pictureUrl = (String) payload.get("picture");
                      String locale = (String) payload.get("locale");
                      String familyName = (String) payload.get("family_name");
                      String givenName = (String) payload.get("given_name");*//*
                      throw new ApiException(-1, "token解析出错或已超时");
                      // Use or store profile information
                      // ...
                  }*/
/***
 * 通过令牌获取token
 */
      /*  GoogleTokenResponse tokenResponse =new GoogleAuthorizationCodeTokenRequest( new NetHttpTransport(),JacksonFactory.getDefaultInstance(),"https://oauth2.googleapis.com/token",
                gooleConfProp.getClientId(),gooleConfProp.getClientSecret(), code, gooleConfProp.getRedirectUrl())
                         // Specify the same redirect URI that you use with your web
                        // app. If you don't have a web version of your app, you can
                        // specify an empty string.
                        .execute();

        HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(httpTransport, jsonFactory)// Specify the CLIENT_ID of the app that accesses the backend:
                .setAudience(Collections.singletonList(gooleConfProp.getClientId()))
                // Or, if multiple clients access the backend:
                //.setAudience(Arrays.asList(CLIENT_ID_1, CLIENT_ID_2, CLIENT_ID_3))
                .build();

        GoogleIdToken idToken=verifier.verify(tokenResponse.getIdToken());
      GoogleIdToken.Payload payload =  idToken.getPayload();*/


        return  true;
    }


}
