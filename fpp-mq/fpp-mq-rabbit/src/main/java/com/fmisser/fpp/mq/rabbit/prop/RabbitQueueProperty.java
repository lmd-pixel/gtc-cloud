package com.fmisser.fpp.mq.rabbit.prop;

import lombok.Data;

/**
 * @author fmisser
 * @create 2021-04-10 下午2:38
 * @description 队列配置
 */

@Data
public class RabbitQueueProperty {
    private String name;
    private boolean durable = true;
    private boolean autoDelete = false;
    private boolean exclusive = false;
    private RabbitQueueArgsProperty arguments;
}
