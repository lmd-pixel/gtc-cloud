package com.fmisser.gtc.push.config;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * @author by fmisser
 * @create 2021/5/25 1:56 下午
 * @description TODO
 */

@Slf4j
@Configuration
@EnableWebSocket
@AllArgsConstructor
public class WebsocketConfig implements WebSocketConfigurer {
    private final String endpoint = "comm";
    private final String identifier = "digit_id";

    @Bean
    public WebsocketHandler websocketHandler() {
        return new WebsocketHandler(endpoint, identifier);
    }

    @Bean
    public WebsocketInterceptor websocketInterceptor() {
        return new WebsocketInterceptor(identifier, null);
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry
                .addHandler(websocketHandler(), endpoint)
                .addInterceptors(websocketInterceptor())
                .setAllowedOrigins("*");
    }
}
