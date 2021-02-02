package com.fmisser.gtc.social.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fmisser.gtc.base.dto.im.ImAfterSendMsgDto;
import com.fmisser.gtc.base.dto.im.ImBeforeSendMsgDto;
import com.fmisser.gtc.base.dto.im.ImCbResp;

import com.fmisser.gtc.base.dto.im.ImStateChangeDto;
import com.fmisser.gtc.base.prop.ImConfProp;
import com.fmisser.gtc.social.service.ImCallbackService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(description = "Tencent IM Callback")
@RestController
@RequestMapping("/im_cb")
public class ImCallbackController {

    private final ObjectMapper objectMapper;

    private final ImCallbackService imCallbackService;

    private final ImConfProp imConfProp;

    public ImCallbackController(ObjectMapper objectMapper,
                                ImCallbackService imCallbackService,
                                ImConfProp imConfProp) {
        this.objectMapper = objectMapper;
        this.imCallbackService = imCallbackService;
        this.imConfProp = imConfProp;
    }

    @PostMapping("/entry")
    public ImCbResp callback(@RequestParam("SdkAppid") long SdkAppid,
                             @RequestParam("CallbackCommand") String CallbackCommand,
                             @RequestParam("contenttype") String json,
                             @RequestParam("ClientIP") String ClientIP,
                             @RequestParam("OptPlatform") String OptPlatform,
                             @RequestBody String content) {
        try {
            // https://cloud.tencent.com/document/product/269/2714
            // TODO: 2020/11/20 考虑此接口的安全性，是否存在被刷的可能

            System.out.println("im recv callback");

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

                System.out.println("im recv msg callback before");

                // 发送消息之前
                ImBeforeSendMsgDto dto = objectMapper.readValue(content, ImBeforeSendMsgDto.class);
                dto.setClientIP(ClientIP);
                dto.setOptPlatform(OptPlatform);
                return imCallbackService.beforeSendMsg(dto);
            } else if (CallbackCommand.equals("C2C.CallbackAfterSendMsg")) {

                System.out.println("im recv msg callback after");

                // 发送消息之后
                ImAfterSendMsgDto dto = objectMapper.readValue(content, ImAfterSendMsgDto.class);
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
