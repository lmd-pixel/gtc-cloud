package com.fmisser.gtc.push.service.impl;

import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.push.config.WebsocketHandler;
import com.fmisser.gtc.push.service.PushService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author by fmisser
 * @create 2021/5/25 3:44 下午
 * @description TODO
 */

@Slf4j
@Service
@AllArgsConstructor
public class PushServiceImpl implements PushService {
    private final WebsocketHandler websocketHandler;

    @Override
    public void broadcastMsg(String content) throws ApiException {
        websocketHandler.broadcastMessage(content);
    }
}
