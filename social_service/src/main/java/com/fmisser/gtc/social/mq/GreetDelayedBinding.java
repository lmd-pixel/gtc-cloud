package com.fmisser.gtc.social.mq;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

/**
 * 骚扰延迟绑定
 */
public interface GreetDelayedBinding {

    String OUTPUT = "greet-delayed-queue-output";
    String INPUT = "greet-delayed-queue-input";

    @Output(OUTPUT)
    MessageChannel greetDelayedOutputChannel();

    @Input(INPUT)
    SubscribableChannel greetDelayedInputChannel();
}
