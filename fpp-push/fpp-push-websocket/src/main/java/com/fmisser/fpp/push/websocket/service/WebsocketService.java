package com.fmisser.fpp.push.websocket.service;

import org.springframework.web.socket.WebSocketSession;

/**
 * @author by fmisser
 * @create 2021/5/24 4:36 下午
 * @description TODO
 */
public interface WebsocketService {
    void sendMessage(WebSocketSession session, String text) throws RuntimeException;

    void sendMessage(WebSocketSession session, Object obj) throws RuntimeException;
}
