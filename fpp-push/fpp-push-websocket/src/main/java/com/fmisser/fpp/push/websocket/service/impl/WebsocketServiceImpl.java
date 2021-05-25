package com.fmisser.fpp.push.websocket.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fmisser.fpp.push.websocket.service.WebsocketService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

/**
 * @author by fmisser
 * @create 2021/5/24 4:37 下午
 * @description
 */

@Slf4j
@Service
@AllArgsConstructor
public class WebsocketServiceImpl implements WebsocketService {
    private final ObjectMapper objectMapper;

    @Override
    public void sendMessage(WebSocketSession session, String text) throws RuntimeException {
        if (!session.isOpen()) {
            log.error("[websocket] send message failed: session is not open");
            throw new RuntimeException("网络连接未建立，消息发送失败");
        }

        TextMessage textMessage = new TextMessage(text);
        try {
            session.sendMessage(textMessage);
        } catch (IOException e) {
            log.error("[websocket] send message failed: {}", e.getMessage());
            throw new RuntimeException("网络连接出错，消息发送失败");
        }
    }

    @Override
    public void sendMessage(WebSocketSession session, Object obj) throws RuntimeException {
        try {
            String jsonString = objectMapper.writeValueAsString(obj);
            sendMessage(session, jsonString);
        } catch (JsonProcessingException e) {
            log.error("[websocket] send message change objct to json failed: {}", e.getMessage());
            throw new RuntimeException("数据格式错误，消息发送失败");
        }
    }
}
