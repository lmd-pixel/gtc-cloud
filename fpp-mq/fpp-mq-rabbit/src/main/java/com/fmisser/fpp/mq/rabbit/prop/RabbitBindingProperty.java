package com.fmisser.fpp.mq.rabbit.prop;

import lombok.Data;
import org.springframework.amqp.core.ExchangeTypes;

/**
 * @author fmisser
 * @create 2021-04-10 下午2:28
 * @description 绑定关系
 */

@Data
public class RabbitBindingProperty {
    private String exchangeName;
    private String exchangeType = ExchangeTypes.TOPIC;
    private boolean exchangeDurable = true;
    private boolean exchangeAutoDelete = false;
    private String routingKey;
    private RabbitExchangeArgsProperty exchangeArgs;
    private RabbitQueueProperty queue;
}
