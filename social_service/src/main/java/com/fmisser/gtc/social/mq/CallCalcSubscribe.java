package com.fmisser.gtc.social.mq;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Header;

import java.io.IOException;
import java.util.Objects;

/**
 * @author fmisser
 * @create 2021-04-13 下午4:16
 * @description
 */

@Slf4j
@EnableBinding(CallCalcBinding.class)
public class CallCalcSubscribe {

    @StreamListener(CallCalcBinding.INPUT)
    public void processMessage(Message<String> message) {
        Channel channel = (Channel) message.getHeaders().get(AmqpHeaders.CHANNEL);
        Long deliveryTag = (Long) message.getHeaders().get(AmqpHeaders.DELIVERY_TAG);
        try {

        } catch (Exception e) {

        } finally {
            if (Objects.isNull(channel) || Objects.isNull(deliveryTag)) {
                log.error("call calc stream ack failed: channel or deliveryTag is null ");
            } else {
                try {
                    channel.basicAck(deliveryTag, false);
                    log.info("call calc stream ack with deliveryTag: {}", deliveryTag);
                } catch (IOException e) {
                    log.error("call calc stream ack failed: {}", e.getMessage());
                }
            }
        }
    }
}
