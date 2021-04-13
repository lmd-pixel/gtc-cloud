package com.fmisser.fpp.mq.rabbit.service.impl;

import com.fmisser.fpp.mq.rabbit.service.RabbitService;
import lombok.SneakyThrows;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.connection.CorrelationData.Confirm;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author fmisser
 * @create 2021-04-12 下午2:22
 * @description
 */

@Service
public class RabbitServiceImpl implements RabbitService {

    private final RabbitTemplate rabbitTemplate;

    public RabbitServiceImpl(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void post(String exchange, String routingKey, Object message) throws RuntimeException {
        rabbitTemplate.convertAndSend(exchange, routingKey, message);
    }

    @Override
    public Confirm syncConfirmSend(String exchange, String routingKey, Object message)
            throws RuntimeException {
        return syncConfirmSend(exchange, routingKey, message, 10, TimeUnit.SECONDS);
    }

    @SneakyThrows
    @Override
    public Confirm syncConfirmSend(String exchange, String routingKey, Object message,
                                   long timeout, TimeUnit unit) throws RuntimeException {
        CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());
        rabbitTemplate.convertAndSend(exchange, routingKey, message, correlationData);
        return correlationData.getFuture().get(timeout, unit);
    }

    @Override
    public Object rpcSend(String exchange, String routingKey, Object message) throws RuntimeException {
        return rabbitTemplate.convertSendAndReceive(exchange, routingKey, message);
    }

    @Override
    public void rpcReply(String exchange, String routingKey, Object message, CorrelationData correlationData)
            throws RuntimeException {
        rabbitTemplate.convertSendAndReceive(exchange, routingKey, message, correlationData);
    }
}
