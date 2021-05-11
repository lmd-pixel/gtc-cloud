package com.fmisser.fpp.thirdparty.apple.feign;

import com.fmisser.fpp.thirdparty.apple.dto.AppleIdLoginAuthKeysResp;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author fmisser
 * @create 2021-05-11 下午3:30
 * @description 获取苹果登录验证的keys
 */

@FeignClient(url = "https://appleid.apple.com/auth/keys", name = "apple-id-login-get-auth-keys")
public interface AppleIdLoginGetAuthKeysFeign {
    @GetMapping(value = "")
    AppleIdLoginAuthKeysResp getAuthKeys();
}
