package com.fmisser.gtc.social.mq;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

/**
 * @author fmisser
 * @create 2021-04-13 下午4:14
 * @description
 */
public interface CallCalcBinding {
    String OUTPUT = "call-calc-output";
    String INPUT = "call-calc-input";

    @Output(OUTPUT)
    MessageChannel callCalcDelayedOutputChannel();

    @Input(INPUT)
    SubscribableChannel callCalcDelayedInputChannel();
}
