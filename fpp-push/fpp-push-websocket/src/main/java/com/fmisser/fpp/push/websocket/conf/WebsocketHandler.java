package com.fmisser.fpp.push.websocket.conf;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author by fmisser
 * @create 2021/5/24 3:21 下午
 * @description TODO
 */

@Slf4j
public class WebsocketHandler extends AbstractWebSocketHandler {

    // endpoint
    private final String endpointName;

    // session 关联的 identifier name
    private final String identifierName;

    private final Map<String, String> sessionIdentifierMap = new ConcurrentHashMap<>();
    private final Map<String, WebSocketSession> sessionMap = new ConcurrentHashMap<>();

    public WebsocketHandler(String endpoint, String identifier) {
        endpointName = endpoint;
        identifierName = identifier;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String identifier = String.valueOf(session.getAttributes().get(identifierName));
        sessionIdentifierMap.put(identifier, session.getId());
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        super.handleMessage(session, message);
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

    }

    @Override
    public void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws Exception {

    }

    @Override
    public void handlePongMessage(WebSocketSession session, PongMessage message) throws Exception {

    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("websocket endpoint: {} transport error with session id: {}, exception: {}. ",
                endpointName, session.getId(), exception.getMessage());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessionMap.remove(session.getId());
    }
}

