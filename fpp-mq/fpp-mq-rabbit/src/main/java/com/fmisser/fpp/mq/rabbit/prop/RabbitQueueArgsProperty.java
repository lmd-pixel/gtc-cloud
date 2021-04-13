package com.fmisser.fpp.mq.rabbit.prop;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author fmisser
 * @create 2021-04-12 下午5:29
 * @description
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RabbitQueueArgsProperty {
    private Long msgTtl;
    private Long maxLen;
    private String dlxExchange;
    private String dlxRoutingKey;
}
