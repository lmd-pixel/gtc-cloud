package com.fmisser.fpp.mq.rabbit.service;

import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.connection.CorrelationData.Confirm;

import java.util.concurrent.TimeUnit;

public interface RabbitService {
    void post(String exchange, String routingKey, Object message) throws RuntimeException;

    Confirm syncConfirmSend(String exchange, String routingKey, Object message) throws RuntimeException;
    Confirm syncConfirmSend(String exchange, String routingKey, Object message,
                                            long timeout, TimeUnit unit) throws RuntimeException;

    Object rpcSend(String exchange, String routingKey, Object message) throws RuntimeException;
    void rpcReply(String exchange, String routingKey, Object message, CorrelationData correlationData)
            throws RuntimeException;
}
