package com.fmisser.fpp.mq.rabbit.conf;

import com.fmisser.fpp.mq.rabbit.prop.RabbitBindingProperty;
import com.fmisser.fpp.mq.rabbit.prop.RabbitExtensionProperty;
import com.fmisser.fpp.mq.rabbit.prop.RabbitQueueArgsProperty;
import com.fmisser.fpp.mq.rabbit.prop.RabbitQueueProperty;
import lombok.AllArgsConstructor;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author fmisser
 * @create 2021-04-10 下午2:57
 * @description rabbit 配置读取
 * @deprecated 建议直接用@Bean方式定义交换机 队列 绑定关系，
 * 因为这里只做了部分属性设置，不全面
 */

@Deprecated
@Configuration
@AllArgsConstructor
public class RabbitConfig {
    private final RabbitExtensionProperty rabbitExtensionProperty;
    private final RabbitTemplate rabbitTemplate;

    @Bean
    public RabbitAdmin rabbitAdmin() {
        // 为了兼容rabbit原始配置，不要使用ConnectionFactory创建RabbitAdmin
        return new RabbitAdmin(rabbitTemplate);
    }

    @PostConstruct
    public void init() {
        // 通过配置定义交换机，队列和绑定关系

        Map<String, RabbitBindingProperty> bindings = rabbitExtensionProperty.getBindings();
        if (Objects.isNull(bindings)) {
            // ignore
            return;
        }
        bindings.values()
                .forEach(rabbitBindingProperty -> {
                    // create & declare exchange
                    Exchange exchange = _innerCreateExchange(rabbitBindingProperty);
                    rabbitAdmin().declareExchange(exchange);

                    // create & declare queue
                    RabbitQueueProperty rabbitQueueProperty = rabbitBindingProperty.getQueue();
                    Queue queue = _innerCreateQueue(rabbitQueueProperty);

                    // 如果定义了死信队列，则创建该死信队列
                    if (queue.getArguments().containsKey("x-dead-letter-exchange")) {
                        String dlxExchangeName = (String) queue.getArguments().get("x-dead-letter-exchange");
                        Exchange dlxExchange = new DirectExchange(dlxExchangeName, true, false, null);
                        rabbitAdmin().declareExchange(dlxExchange);

                        String dlxQueueName = String.format("%s.dlq", rabbitQueueProperty.getName());
                        Queue dlxQueue = new Queue(dlxQueueName, true, false, false, null);
                        rabbitAdmin().declareQueue(dlxQueue);

                        String dlxRoutingKey = "";
                        if (queue.getArguments().containsKey("x-dead-letter-routing-key")) {
                            dlxRoutingKey = (String) queue.getArguments().get("x-dead-letter-routing-key");
                        }
                        Binding dlxBinding = _innerCreateBinding(dlxQueueName, dlxExchangeName, dlxRoutingKey);
                        rabbitAdmin().declareBinding(dlxBinding);
                    }

                    rabbitAdmin().declareQueue(queue);

                    // create & declare binding
                    Binding binding = _innerCreateBinding(
                            queue.getName(),
                            exchange.getName(),
                            rabbitBindingProperty.getRoutingKey());
                    rabbitAdmin().declareBinding(binding);
                });
    }

    /**
     * 创建binding
     */
    private Binding _innerCreateBinding(String queueName, String exchangeName, String routingKey) {
        return new Binding(
                queueName,
                Binding.DestinationType.QUEUE,  // 只针对queue
                exchangeName,
                routingKey, null);
    }

    /**
     * 创建queue
     */
    private Queue _innerCreateQueue(RabbitQueueProperty rabbitQueueProperty) {
        // params
        Map<String, Object> queueArgs = new HashMap<>();
        RabbitQueueArgsProperty rabbitQueueArgsProperty = rabbitQueueProperty.getArguments();
        if (Objects.nonNull(rabbitQueueArgsProperty)) {
            if (Objects.nonNull(rabbitQueueArgsProperty.getMsgTtl())) {
                queueArgs.put("x-message-ttl", rabbitQueueArgsProperty.getMsgTtl());
            }
            if (Objects.nonNull(rabbitQueueArgsProperty.getMaxLen())) {
                queueArgs.put("x-max-length", rabbitQueueArgsProperty.getMsgTtl());
            }
            if (Objects.nonNull(rabbitQueueArgsProperty.getDlxExchange())) {
                queueArgs.put("x-dead-letter-exchange", rabbitQueueArgsProperty.getDlxExchange());
            }
            if (Objects.nonNull(rabbitQueueArgsProperty.getDlxRoutingKey())) {
                queueArgs.put("x-dead-letter-routing-key", rabbitQueueArgsProperty.getDlxRoutingKey());
            }
        }
        return new Queue(
                rabbitQueueProperty.getName(),
                rabbitQueueProperty.isDurable(),
                rabbitQueueProperty.isExclusive(),
                rabbitQueueProperty.isAutoDelete(),
                queueArgs);
    }

    /**
     * 创建交换机
     */
    private Exchange _innerCreateExchange(RabbitBindingProperty rabbitBindingProperty) {
        String exchangeType = rabbitBindingProperty.getExchangeType();

        // params
        Map<String, Object> exchangeArgs = new HashMap<>();
        if (Objects.nonNull(rabbitBindingProperty.getExchangeArgs()) &&
                rabbitBindingProperty.getExchangeArgs().getDelayed()) {
            // 延迟消息交换机
            // 需要插件支持：rabbitmq_delayed_message_exchange
            exchangeArgs.put("x-delayed-type", exchangeType);
        }

        switch (exchangeType) {
            case ExchangeTypes.DIRECT:
                return new DirectExchange(
                        rabbitBindingProperty.getExchangeName(),
                        rabbitBindingProperty.isExchangeDurable(),
                        rabbitBindingProperty.isExchangeAutoDelete(),
                        exchangeArgs);
            case ExchangeTypes.TOPIC:
                return new TopicExchange(
                        rabbitBindingProperty.getExchangeName(),
                        rabbitBindingProperty.isExchangeDurable(),
                        rabbitBindingProperty.isExchangeAutoDelete(),
                        exchangeArgs);
            case ExchangeTypes.FANOUT:
                return new FanoutExchange(
                        rabbitBindingProperty.getExchangeName(),
                        rabbitBindingProperty.isExchangeDurable(),
                        rabbitBindingProperty.isExchangeAutoDelete(),
                        exchangeArgs);
            default:
                throw new UnsupportedOperationException();
        }
    }
}
