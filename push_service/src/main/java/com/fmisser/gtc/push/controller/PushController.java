package com.fmisser.gtc.push.controller;

import com.fmisser.gtc.base.response.ApiResp;
import com.fmisser.gtc.push.service.PushService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author by fmisser
 * @create 2021/5/25 5:12 下午
 * @description TODO
 */

@RestController
@RequestMapping("/push")
@AllArgsConstructor
public class PushController {
    private final PushService pushService;

    @GetMapping("/broadcast")
    public ApiResp<Integer> broadcast(@RequestParam("content") String content) {
        pushService.broadcastMsg(content);
        return ApiResp.succeed(1);
    }
}
