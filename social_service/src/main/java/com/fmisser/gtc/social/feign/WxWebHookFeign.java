package com.fmisser.gtc.social.feign;

import com.fmisser.gtc.base.dto.wx.WebHookDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(url = "https://qyapi.weixin.qq.com/cgi-bin", name = "wx-web-hook")
@Service
public interface WxWebHookFeign {
    @PostMapping(value = "/webhook/send?key=db789ee4-4ca2-46c1-8df7-08dc33db1e5a")
    String postMsg(@RequestBody WebHookDto webHookDto);
}
