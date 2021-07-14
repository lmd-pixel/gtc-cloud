package com.fmisser.gtc.social.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fmisser.fpp.cache.redis.service.RedisService;
import com.fmisser.gtc.base.dto.im.ImAfterSendMsgDto;
import com.fmisser.gtc.base.dto.im.ImBeforeSendMsgDto;
import com.fmisser.gtc.base.dto.im.ImCbResp;

import com.fmisser.gtc.base.dto.im.ImStateChangeDto;
import com.fmisser.gtc.base.prop.ImConfProp;
import com.fmisser.gtc.social.service.ImCallbackService;
import com.fmisser.gtc.social.service.ModerationService;
import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@Api(description = "Tencent IM Callback")
@RestController
@RequestMapping("/im_cb")
@AllArgsConstructor
@Slf4j
public class ImCallbackController {

    private final ObjectMapper objectMapper;
    private final ImCallbackService imCallbackService;
    private final ImConfProp imConfProp;

    @GetMapping
    public Integer testTextModeration(@RequestParam("userId") String userId,
                                      @RequestParam("content") String content) {
        return imCallbackService.textModeration(userId, content, "", 0);
    }

    @PostMapping("/entry")
    public Object callback(@RequestParam("SdkAppid") long SdkAppid,
                             @RequestParam("CallbackCommand") String CallbackCommand,
                             @RequestParam("contenttype") String json,
                             @RequestParam("ClientIP") String ClientIP,
                             @RequestParam("OptPlatform") String OptPlatform,
                             @RequestBody String content) {
        try {
            // https://cloud.tencent.com/document/product/269/2714
            // TODO: 2020/11/20 考虑此接口的安全性，是否存在被刷的可能

            // check SdkAppid
            if (SdkAppid != imConfProp.getSdkAppId()) {
                return new ImCbResp();
            }

            // 根据 CallbackCommand 解析不通的内容
            if (CallbackCommand.equals("State.StateChange")) {
                // 用户状态更新
                ImStateChangeDto dto = objectMapper.readValue(content, ImStateChangeDto.class);

                dto.setClientIP(ClientIP);
                dto.setOptPlatform(OptPlatform);
                return imCallbackService.stateChangeCallback(dto);
            } else if (CallbackCommand.equals("C2C.CallbackBeforeSendMsg")) {
                // 发送消息之前
                // TODO: 2021/6/1 消息的解析这里不完善，具体消息格式较复杂
                ImBeforeSendMsgDto dto = objectMapper.readValue(content, ImBeforeSendMsgDto.class);
                dto.setClientIP(ClientIP);
                dto.setOptPlatform(OptPlatform);
                Object obj = imCallbackService.beforeSendMsg(dto, content);
                log.info("[imcb] before send msg return object: {}", objectMapper.writeValueAsString(obj));
                return obj;
            } else if (CallbackCommand.equals("C2C.CallbackAfterSendMsg")) {

                // 发送消息之后
                // TODO: 2021/6/1 消息的解析这里不完善，具体消息格式较复杂
                ImAfterSendMsgDto dto = objectMapper.readValue(content, ImAfterSendMsgDto.class);
                dto.setClientIP(ClientIP);
                dto.setOptPlatform(OptPlatform);
                return imCallbackService.afterSendMsg(dto, content);
            } else {
                return new ImCbResp();
            }
        } catch (Exception e) {
            // 异常直接返回
            return new ImCbResp();
        }
    }
}
