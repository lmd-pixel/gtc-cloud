package com.fmisser.gtc.push.event;

import org.springframework.context.ApplicationEvent;

/**
 * @author by fmisser
 * @create 2021/5/25 2:36 下午
 * @description websocket message event
 */
public class WebsocketMessageEvent extends ApplicationEvent {
    public WebsocketMessageEvent(Object source) {
        super(source);
    }
}
