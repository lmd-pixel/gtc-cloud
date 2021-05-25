package com.fmisser.fpp.push.websocket.conf;

import org.springframework.web.socket.WebSocketMessage;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author by fmisser
 * @create 2021/5/24 6:02 下午
 * @description
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface WsMsgHandler {
    Class<? extends WebSocketMessage>[] type() default {};
}
