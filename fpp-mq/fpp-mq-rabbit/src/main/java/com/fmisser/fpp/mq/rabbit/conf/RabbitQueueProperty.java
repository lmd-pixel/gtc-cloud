package com.fmisser.fpp.mq.rabbit.conf;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

/**
 * @author fmisser
 * @create 2021-04-10 下午2:38
 * @description
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RabbitQueueProperty {
    private String name;
    private boolean durable = true;
    private boolean autoDelete = false;
    private boolean exclusive = false;
    private String routingKey;
    private Map<String, Object> arguments;
}
