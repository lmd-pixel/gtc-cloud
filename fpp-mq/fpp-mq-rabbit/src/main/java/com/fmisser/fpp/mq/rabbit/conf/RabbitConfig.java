package com.fmisser.fpp.mq.rabbit.conf;

import com.fmisser.fpp.mq.rabbit.prop.RabbitProp;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Objects;

/**
 * @author fmisser
 * @create 2021-04-10 下午2:57
 * @description
 */

@Configuration
public class RabbitConfig {
    private final RabbitProp rabbitProp;
    private final ConnectionFactory connectionFactory;

    public RabbitConfig(RabbitProp rabbitProp,
                        ConnectionFactory connectionFactory) {
        this.rabbitProp = rabbitProp;
        this.connectionFactory = connectionFactory;
    }

    @Bean
    public RabbitAdmin rabbitAdmin() {
        return new RabbitAdmin(connectionFactory);
    }

    @PostConstruct
    public void init() {
        Map<String, RabbitExchangeProperty> exchanges = rabbitProp.getExchanges();
        if (Objects.isNull(exchanges)) {
            // ignore
        }
        exchanges.values()
                .forEach(rabbitExchangeProperty -> {
                    Exchange exchange = _innerCreateExchange(rabbitExchangeProperty);
                    rabbitAdmin().declareExchange(exchange);
                    rabbitExchangeProperty.getQueues().values()
                            .forEach(rabbitQueueProperty -> {
                                Queue queue = _innerCreateQueue(rabbitQueueProperty);
                                rabbitAdmin().declareQueue(queue);

                                Binding binding = _innerCreateBinding(
                                        queue.getName(),
                                        exchange.getName(),
                                        rabbitQueueProperty.getRoutingKey());
                                rabbitAdmin().declareBinding(binding);
                            });
                });
    }

    /**
     * 创建binding
     */
    private Binding _innerCreateBinding(String queueName, String exchangeName, String routingKey) {
        return new Binding(
                queueName,
                Binding.DestinationType.QUEUE,
                exchangeName,
                routingKey, null);
    }

    /**
     * 创建queue
     */
    private Queue _innerCreateQueue(RabbitQueueProperty rabbitQueueProperty) {
        return new Queue(
                rabbitQueueProperty.getName(),
                rabbitQueueProperty.isDurable(),
                rabbitQueueProperty.isExclusive(),
                rabbitQueueProperty.isAutoDelete(),
                rabbitQueueProperty.getArguments());
    }

    /**
     * 创建交换机
     */
    private Exchange _innerCreateExchange(RabbitExchangeProperty rabbitExchangeProperty) {
        String exchangeType = rabbitExchangeProperty.getType();
        switch (exchangeType) {
            case ExchangeTypes.DIRECT:
                return new DirectExchange(
                        rabbitExchangeProperty.getName(),
                        rabbitExchangeProperty.isDurable(),
                        rabbitExchangeProperty.isAutoDelete(),
                        rabbitExchangeProperty.getArguments());
            case ExchangeTypes.TOPIC:
                return new TopicExchange(
                        rabbitExchangeProperty.getName(),
                        rabbitExchangeProperty.isDurable(),
                        rabbitExchangeProperty.isAutoDelete(),
                        rabbitExchangeProperty.getArguments());
            case ExchangeTypes.FANOUT:
                return new FanoutExchange(
                        rabbitExchangeProperty.getName(),
                        rabbitExchangeProperty.isDurable(),
                        rabbitExchangeProperty.isAutoDelete(),
                        rabbitExchangeProperty.getArguments());
            default:
                throw new UnsupportedOperationException();
        }
    }
}
