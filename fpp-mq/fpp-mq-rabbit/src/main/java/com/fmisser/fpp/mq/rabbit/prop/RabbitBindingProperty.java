package com.fmisser.fpp.mq.rabbit.prop;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.amqp.core.ExchangeTypes;

/**
 * @author fmisser
 * @create 2021-04-10 下午2:28
 * @description
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RabbitBindingProperty {
    private String exchangeName;
    private String exchangeType = ExchangeTypes.TOPIC;
    private boolean exchangeDurable = true;
    private boolean exchangeAutoDelete = false;
    private String routingKey;
    private RabbitExchangeArgsProperty exchangeArgs;
    private RabbitQueueProperty queue;
}
