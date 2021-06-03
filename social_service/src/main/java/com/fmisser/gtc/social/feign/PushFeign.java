package com.fmisser.gtc.social.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author by fmisser
 * @create 2021/6/2 7:41 下午
 * @description TODO
 */
@FeignClient(name = "push")
public interface PushFeign {

    @PostMapping("/broadcast")
    public void broadcast(@RequestParam("content") String content);
}
