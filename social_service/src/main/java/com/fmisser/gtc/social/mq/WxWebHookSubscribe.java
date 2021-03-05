package com.fmisser.gtc.social.mq;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.Message;

/**
 * 微信 web hook
 */

@EnableBinding
public class WxWebHookSubscribe {

    @StreamListener(WxWebHookBinding.INPUT)
    public void processMessage(Message<String> message) throws Exception {
        Channel channel = (Channel) message.getHeaders().get(AmqpHeaders.CHANNEL);
        Long deliveryTag = (Long) message.getHeaders().get(AmqpHeaders.DELIVERY_TAG);

        String payload = message.getPayload();
    }
}
