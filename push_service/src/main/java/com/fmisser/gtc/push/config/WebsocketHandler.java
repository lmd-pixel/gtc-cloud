package com.fmisser.gtc.push.config;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author by fmisser
 * @create 2021/5/25 1:46 下午
 * @description TODO
 */

@Slf4j
@EqualsAndHashCode(callSuper = true)
@Data
public class WebsocketHandler extends AbstractWebSocketHandler {
    // endpoint
    private final String endpointName;
    // session 关联的 identifier name
    private final String identifierName;

//    private final Map<String, String> sessionIdentifierMap = new ConcurrentHashMap<>();
//    private final Map<String, WebSocketSession> sessionMap = new ConcurrentHashMap<>();

    private final Map<String, String> sessionIdentifierMap = new HashMap<>();
    private final Map<String, WebSocketSession> sessionMap = new HashMap<>();
    ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();

    public WebsocketHandler(String endpoint, String identifier) {
        endpointName = endpoint;
        identifierName = identifier;

        threadPoolTaskExecutor.setCorePoolSize(8);
        threadPoolTaskExecutor.setMaxPoolSize(20);
        threadPoolTaskExecutor.setKeepAliveSeconds(30);
        threadPoolTaskExecutor.setQueueCapacity(Integer.MAX_VALUE);
        threadPoolTaskExecutor.initialize();
    }

    // 发送消息
    public void broadcastMessage(String content) {

        // copy all session
        List<WebSocketSession> sessionIds = new ArrayList<>(sessionMap.values());

        sessionIds.parallelStream()
                .forEach(session -> {
                    ListenableFuture<Boolean> listenableFuture = threadPoolTaskExecutor.submitListenable(() -> {
                        if (Objects.nonNull(session) && session.isOpen()) {
                            TextMessage textMessage = new TextMessage(content);
                            session.sendMessage(textMessage);
                            return true;
                        } else {
                            return false;
                        }
                    });
                    listenableFuture.addCallback(result -> {
                        if (Objects.nonNull(result) && !result) {
                            // 出错了
                            if (Objects.isNull(session)) {
                                log.error("[websocket] send message failed, session is null");
                            } else if (!session.isOpen()) {
                                log.error("[websocket] send message failed, session: {} is close", session.getId());
                            }
                        }
                    }, ex -> {
                        log.error("[websocket] send message failed: {}", ex.getMessage());
                    });
                });
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String identifier = String.valueOf(session.getAttributes().get(identifierName));
        log.info("[websocket] connected with identifier: {} and session id: {}", identifier, session.getId());

        lockPutSession(session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

    }

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws Exception {

    }

    @Override
    protected void handlePongMessage(WebSocketSession session, PongMessage message) throws Exception {
        session.sendMessage(new PingMessage());
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("[websocket] endpoint: {} transport error with session id: {}, exception: {}. ",
                endpointName, session.getId(), exception.getMessage());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String identifier = String.valueOf(session.getAttributes().get(identifierName));
        log.info("[websocket] close session with identifier: {} and session id: {}", identifier, session.getId());

        lockRemoveSession(session);
    }

    private WebSocketSession lockGetSession(String identifier) {
        Lock readLock = readWriteLock.readLock();
        try {
            readLock.lock();

            String sessionId = sessionIdentifierMap.get(identifier);
            return sessionMap.get(sessionId);
        } finally {
            readLock.unlock();
        }
    }

    private void lockPutSession(WebSocketSession session) {
        Lock writeLock = readWriteLock.writeLock();
        try {
            writeLock.lock();

            String identifier = String.valueOf(session.getAttributes().get(identifierName));
            sessionIdentifierMap.put(identifier, session.getId());
            sessionMap.put(session.getId(), session);

        } finally {
            writeLock.unlock();
        }
    }

    private void lockRemoveSession(WebSocketSession session) {
        Lock writeLock = readWriteLock.writeLock();
        try {
            writeLock.lock();

            String identifier = String.valueOf(session.getAttributes().get(identifierName));
            sessionIdentifierMap.remove(identifier);
            sessionMap.remove(session.getId());
        } finally {
            writeLock.unlock();
        }
    }
}
