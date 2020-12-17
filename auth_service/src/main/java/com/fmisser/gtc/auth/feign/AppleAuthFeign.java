package com.fmisser.gtc.auth.feign;

import com.fmisser.gtc.base.dto.apple.PublicKeysDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URI;

/**
 * 苹果认证
 */

@FeignClient(url = "https://www.apple.com", name = "apple-auth")
@Service
public interface AppleAuthFeign {
    // 获取public keys
    @GetMapping(value = "")
    PublicKeysDto getAuthKeys(URI uri);
}

