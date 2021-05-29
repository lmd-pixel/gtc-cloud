package com.fmisser.gtc.push.event;

import com.fmisser.gtc.push.config.WebsocketHandler;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.SmartApplicationListener;
import org.springframework.stereotype.Component;

/**
 * @author by fmisser
 * @create 2021/5/25 2:42 下午
 * @description TODO
 */

@Component
public class WebsocketMessageListener implements SmartApplicationListener {
    @Override
    public boolean supportsEventType(Class<? extends ApplicationEvent> eventType) {
        if (eventType.equals(WebsocketMessageEvent.class)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean supportsSourceType(Class<?> sourceType) {
        if (sourceType.equals(WebsocketHandler.class)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        WebsocketHandler handler = (WebsocketHandler) event.getSource();


    }
}
