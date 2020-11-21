package com.fmisser.gtc.social.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fmisser.gtc.base.dto.im.ImAfterSendMsgDto;
import com.fmisser.gtc.base.dto.im.ImBeforeSendMsgDto;
import com.fmisser.gtc.base.dto.im.ImCbResp;

import com.fmisser.gtc.base.dto.im.ImStateChangeDto;
import com.fmisser.gtc.social.service.ImCallbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/im_cb")
public class ImCallbackController {

    private final ObjectMapper objectMapper;

    private final ImCallbackService imCallbackService;

    public ImCallbackController(ObjectMapper objectMapper,
                                ImCallbackService imCallbackService) {
        this.objectMapper = objectMapper;
        this.imCallbackService = imCallbackService;
    }

    @PostMapping("/")
    public ImCbResp callback(@RequestParam("SdkAppid") String SdkAppid,
                             @RequestParam("CallbackCommand") String CallbackCommand,
                             @RequestParam("contenttype") String jsonContent,
                             @RequestParam("ClientIP") String ClientIP,
                             @RequestParam("OptPlatform") String OptPlatform) {
        try {
            // https://cloud.tencent.com/document/product/269/2714
            // TODO: 2020/11/20 考虑此接口的安全性，是否存在被刷的可能

            // check SdkAppid

            // 根据 CallbackCommand 解析不通的内容
            if (CallbackCommand.equals("State.StateChange")) {
                // 用户状态更新
                ImStateChangeDto dto = objectMapper.readValue(jsonContent, ImStateChangeDto.class);
                dto.setClientIP(ClientIP);
                dto.setOptPlatform(OptPlatform);
                return imCallbackService.stateChangeCallback(dto);
            } else if (CallbackCommand.equals("C2C.CallbackBeforeSendMsg")) {
                // 发送消息之前
                ImBeforeSendMsgDto dto = objectMapper.readValue(jsonContent, ImBeforeSendMsgDto.class);
                dto.setClientIP(ClientIP);
                dto.setOptPlatform(OptPlatform);
                return imCallbackService.beforeSendMsg(dto);
            } else if (CallbackCommand.equals("C2C.CallbackAfterSendMsg")) {
                // 发送消息之后
                ImAfterSendMsgDto dto = objectMapper.readValue(jsonContent, ImAfterSendMsgDto.class);
                dto.setClientIP(ClientIP);
                dto.setOptPlatform(OptPlatform);
                return imCallbackService.afterSendMsg(dto);
            } else {
                return new ImCbResp();
            }
        } catch (Exception e) {
            // 异常直接返回
            return new ImCbResp();
        }
    }
}
