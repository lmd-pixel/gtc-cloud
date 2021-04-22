package com.fmisser.fpp.mq.rabbit.prop;

import lombok.Data;

/**
 * @author fmisser
 * @create 2021-04-12 下午5:29
 * @description 队列其他参数配置
 */

@Data
public class RabbitQueueArgsProperty {
    private Long msgTtl;
    private Long maxLen;
    private String dlxExchange;
    private String dlxRoutingKey;
}
