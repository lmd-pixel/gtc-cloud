package com.fmisser.fpp.push.websocket.conf;

import lombok.AllArgsConstructor;
import org.springframework.aop.TargetClassAware;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

/**
 * @author by fmisser
 * @create 2021/5/24 3:12 下午
 * @description TODO
 */
@Configuration
@EnableWebSocket
@AllArgsConstructor
public class WebsocketConfig implements WebSocketConfigurer {
    private final ApplicationContext applicationContext;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {

        Map<String, Object> beans = applicationContext.getBeansWithAnnotation(WsMsgBinding.class);
        beans.values().stream()
                .flatMap(o -> Arrays.stream(ReflectionUtils.getAllDeclaredMethods(AopUtils.getTargetClass(o))))
                .filter(method -> method.isAnnotationPresent(WsMsgEndpoint.class))
                .forEach(method -> {
                    String endpoint = (String) AnnotationUtils
                            .getValue(method.getDeclaredAnnotation(WsMsgEndpoint.class), "value");
                    String id = (String) AnnotationUtils
                            .getValue(method.getDeclaredAnnotation(WsMsgEndpoint.class), "id");
                    Boolean auth = (Boolean) AnnotationUtils
                            .getValue(method.getDeclaredAnnotation(WsMsgEndpoint.class), "auth");
                    String authName = (String) AnnotationUtils
                            .getValue(method.getDeclaredAnnotation(WsMsgEndpoint.class), "authName");
                    String[] params = (String[]) AnnotationUtils
                            .getValue(method.getDeclaredAnnotation(WsMsgEndpoint.class), "params");
                    Boolean sockJs = (Boolean) AnnotationUtils
                            .getValue(method.getDeclaredAnnotation(WsMsgEndpoint.class), "sockJs");

                    if (Objects.isNull(auth)) {
                        auth = false;
                    }

                    if (Objects.isNull(sockJs)) {
                        sockJs = false;
                    }

                    if (sockJs) {
                        webSocketHandlerRegistry
                                .addHandler(new WebsocketHandler(endpoint, id), endpoint)
                                .addInterceptors(new WebsocketInterceptor(id, auth, authName, params))
                                .setAllowedOrigins("*")
                                .withSockJS();
                    } else {
                        webSocketHandlerRegistry
                                .addHandler(new WebsocketHandler(endpoint, id), endpoint)
                                .addInterceptors(new WebsocketInterceptor(id, auth, authName, params))
                                .setAllowedOrigins("*");
                    }
                });
    }
}
