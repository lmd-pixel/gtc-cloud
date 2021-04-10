package com.fmisser.fpp.mq.rabbit.conf;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.amqp.core.ExchangeTypes;

import java.util.Map;

/**
 * @author fmisser
 * @create 2021-04-10 下午2:28
 * @description
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RabbitExchangeProperty {
    private String name;
    private String type = ExchangeTypes.TOPIC;
    private boolean durable = true;
    private boolean autoDelete = false;
    private Map<String, Object> arguments;
    private Map<String, RabbitQueueProperty> queues;
}
