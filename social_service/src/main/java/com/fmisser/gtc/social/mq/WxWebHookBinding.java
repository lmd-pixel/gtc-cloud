package com.fmisser.gtc.social.mq;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

/**
 * 微信通知
 */
public interface WxWebHookBinding {
    String OUTPUT = "wx-web-hook-queue-output";
    String INPUT = "wx-web-hook-queue-input";

    @Output(OUTPUT)
    MessageChannel wxWebHookOutputChannel();

    @Input(INPUT)
    SubscribableChannel wxWebHookInputChannel();
}
