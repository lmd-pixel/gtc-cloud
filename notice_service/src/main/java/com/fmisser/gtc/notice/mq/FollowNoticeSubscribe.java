package com.fmisser.gtc.notice.mq;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.Message;

import java.io.IOException;

@EnableBinding(FollowNoticeSubscribeBinding.class)
public class FollowNoticeSubscribe {

    @StreamListener(target = "follow-notice-channel")
    public void processMessage(Message<Long> message) throws IOException {
        // TODO: 2020/10/28 在服务重启的时候 已经处理过的消息可能会再次接收，根据业务需要处理幂等性
        Channel channel = (Channel) message.getHeaders().get(AmqpHeaders.CHANNEL);
        Long deliveryTag = (Long) message.getHeaders().get(AmqpHeaders.DELIVERY_TAG);
        System.out.println("listener1 receive message youndId=" + message.toString());
//        channel.basicAck(deliveryTag, false);
//        channel.basicNack(deliveryTag, false, false);
        // requeue等于false时，开启了dlq，直接去dlq
//        channel.basicReject(deliveryTag, false);
        try {
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

