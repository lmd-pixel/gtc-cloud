package com.fmisser.gtc.social.feign;


import com.fmisser.gtc.base.dto.im.ImQueryStateDto;
import com.fmisser.gtc.base.dto.im.ImQueryStateResp;
import com.fmisser.gtc.base.dto.im.ImSendMsgCbResp;
import com.fmisser.gtc.base.dto.im.ImSendMsgDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

/**
 * im 服务
 */

@FeignClient(url = "https://console.tim.qq.com", name = "tencent-im")
@Service
public interface ImFeign {
    // 发送单聊消息
    @PostMapping(value = "/v4/openim/sendmsg", produces = MediaType.APPLICATION_JSON_VALUE)
    ImSendMsgCbResp sendMsg(@RequestParam("sdkappid") long sdkappid,
                            @RequestParam("identifier") String identifier,
                            @RequestParam("usersig") String usersig,
                            @RequestParam("random") int random,
                            @RequestParam("contenttype") String contenttype,
                            @RequestBody ImSendMsgDto imSendMsgDto);

    // 查询用户状态
    @PostMapping(value = "/v4/openim/querystate", produces = MediaType.APPLICATION_JSON_VALUE)
    ImQueryStateResp queryState(@RequestParam("sdkappid") long sdkappid,
                                @RequestParam("identifier") String identifier,
                                @RequestParam("usersig") String usersig,
                                @RequestParam("random") int random,
                                @RequestParam("contenttype") String contenttype,
                                @RequestBody ImQueryStateDto imQueryStateDto);
}
