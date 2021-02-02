package com.fmisser.gtc.social.mq;

import com.fmisser.gtc.social.domain.User;
import com.fmisser.gtc.social.service.GreetService;
import com.fmisser.gtc.social.service.ImService;
import com.fmisser.gtc.social.service.UserService;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.Message;

import java.io.IOException;

/**
 * 骚扰延迟消息接收
 */

@EnableBinding(GreetDelayedBinding.class)
public class GreetDelayedSubscribe {

    @Autowired
    private GreetService greetService;

    @Autowired
    private UserService userService;

    @Autowired
    private ImService imService;

    @StreamListener(GreetDelayedBinding.INPUT)
    public void processMessage(Message<String> message) throws Exception {
        Channel channel = (Channel) message.getHeaders().get(AmqpHeaders.CHANNEL);
        Long deliveryTag = (Long) message.getHeaders().get(AmqpHeaders.DELIVERY_TAG);

        String payload = message.getPayload();
        String[] result = payload.split(",");

        int type = Integer.parseInt(result[0]);
        if (type == 1) {
            // 发送聊天消息
            Long anchorId = Long.parseLong(result[1]);
            Long userId = Long.parseLong(result[2]);
            String content = result[3];
            User userFrom = userService.getUserById(anchorId);
            User userTo = userService.getUserById(userId);
            imService.sendToUser(userFrom, userTo, content);
        } else if (type == 2) {
            // 生成延迟事件
            String userDigitId = result[1];
            int stage = Integer.parseInt(result[2]);
            User user = userService.getUserByDigitId(userDigitId);
            if (!greetService.isTodayReply(user)) {
                greetService.updateGreet(user, stage + 1);
            }
        } else if (type == 3) {
            // 发送系统消息
            Long userId = Long.parseLong(result[2]);
            String content = result[3];
            User userTo = userService.getUserById(userId);
            imService.sendToUser(null, userTo, content);
        }

        channel.basicAck(deliveryTag, false);
    }
}
