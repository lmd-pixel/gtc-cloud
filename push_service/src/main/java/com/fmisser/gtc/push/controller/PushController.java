package com.fmisser.gtc.push.controller;

import com.fmisser.gtc.base.response.ApiResp;
import com.fmisser.gtc.push.service.PushService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * @author by fmisser
 * @create 2021/5/25 5:12 下午
 * @description TODO
 */

@RestController
@RequestMapping("/")
@AllArgsConstructor
public class PushController {
    private final PushService pushService;

    @PostMapping("/broadcast")
    public ApiResp<Integer> broadcast(@RequestParam("content") String content) {
        pushService.broadcastMsg(content);
        return ApiResp.succeed(1);
    }

    @GetMapping("/broadcast")
    public ApiResp<Integer> broadcast2(@RequestParam("content") String content) {
        pushService.broadcastMsg(content);
        return ApiResp.succeed(1);
    }
}
