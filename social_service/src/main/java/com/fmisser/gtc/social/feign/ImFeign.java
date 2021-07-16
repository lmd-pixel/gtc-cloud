package com.fmisser.gtc.social.feign;


import com.fmisser.gtc.base.dto.im.*;
import feign.Headers;
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
    @PostMapping(value = "/v4/openim/sendmsg", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
//    @Headers({"Accept: application/json", "Content-Type: application/json"})
    ImSendMsgCbResp sendMsg(@RequestParam("sdkappid") long sdkappid,
                            @RequestParam("identifier") String identifier,
                            @RequestParam("usersig") String usersig,
                            @RequestParam("random") int random,
                            @RequestParam("contenttype") String contenttype,
                            @RequestBody ImSendMsgDto imSendMsgDto);

    // 查询用户状态
    @PostMapping(value = "/v4/openim/querystate", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    ImQueryStateResp queryState(@RequestParam("sdkappid") long sdkappid,
                                @RequestParam("identifier") String identifier,
                                @RequestParam("usersig") String usersig,
                                @RequestParam("random") int random,
                                @RequestParam("contenttype") String contenttype,
                                @RequestBody ImQueryStateDto imQueryStateDto);

    // 设置用户资料
    @PostMapping(value = "/v4/profile/portrait_set", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    ImProfileSetResp setProfile(@RequestParam("sdkappid") long sdkappid,
                                @RequestParam("identifier") String identifier,
                                @RequestParam("usersig") String usersig,
                                @RequestParam("random") int random,
                                @RequestParam("contenttype") String contenttype,
                                @RequestBody ImProfileSetReq imProfileSetReq);
}
