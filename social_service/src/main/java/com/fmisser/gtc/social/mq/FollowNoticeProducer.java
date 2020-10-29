package com.fmisser.gtc.social.mq;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.cloud.stream.annotation.EnableBinding;

/**
 * 生产者消息配置
 * 消息的处理并没有经过RabbitTemplate（通过SCS）, 所以回调不会执行
 */

@EnableBinding(value = {FollowNoticeBinding.class})
public class FollowNoticeProducer implements RabbitTemplate.ConfirmCallback, RabbitTemplate.ReturnCallback{
    private final RabbitTemplate rabbitTemplate;

    public FollowNoticeProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;

        this.rabbitTemplate.setConfirmCallback(this);
        this.rabbitTemplate.setReturnCallback(this);
    }

    @Override
    public void confirm(CorrelationData correlationData, boolean b, String s) {
        System.out.println("确认消息：" + correlationData);
        System.out.println("消息是否到达：" + b);
        System.out.println("消息失败的原因：" + s);
    }

    @Override
    public void returnedMessage(Message message, int i, String s, String s1, String s2) {
        System.out.println("消息入队列失败：" + message);
    }
}
