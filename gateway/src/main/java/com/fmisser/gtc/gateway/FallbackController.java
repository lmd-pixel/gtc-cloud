package com.fmisser.gtc.gateway;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * hystrix 降级服务
 */

@RestController
public class FallbackController {
    @GetMapping("/fallback")
    public Object fallback() {
        Map<String,Object> result = new HashMap<>();
        result.put("data", null);
        result.put("code", 500);
        result.put("message", "get request fallback");
        return result;
    }
}
