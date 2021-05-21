package com.fmisser.gtc.social.controller;

import com.fmisser.fpp.cache.redis.service.RedisService;
import com.fmisser.fpp.oss.abs.service.OssService;
import com.fmisser.gtc.base.dto.im.ImMsgBody;
import com.fmisser.gtc.base.dto.im.ImMsgFactory;
import com.fmisser.gtc.base.dto.im.ImSendMsgDto;
import com.fmisser.gtc.social.mq.WxWebHookBinding;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(description = "TEST API")
@RequestMapping("/test")
@RestController
@AllArgsConstructor
public class TestController {

    private final WxWebHookBinding wxWebHookBinding;
    private final OssService ossService;
    private final RedisService redisService;

    @GetMapping("/slow")
    public Object slowTest() {
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "ok";
    }

    @PostMapping("/post-msg")
    public Object postMsg() {
        String message = String.format("1,%d,%d", 933, 22);
        Message<String> tipMsg = MessageBuilder.withPayload(message).build();
        return wxWebHookBinding.wxWebHookOutputChannel().send(tipMsg);
    }

    @ApiOperation("fpp-test")
    @GetMapping("fpp-test")
    public String fppTest() {
        return ossService.getName();
    }

    @ApiOperation("redis-test")
    @GetMapping("redis-test")
    public Object redisTest() {
        redisService.listLeftPush("test-object",
                ImMsgFactory.buildCallMsg("123", "456", "789", "000", false, false));
        ImSendMsgDto msgDto = (ImSendMsgDto) redisService.listRightPop("test-object");
        return msgDto.getFrom_Account();
    }

}
