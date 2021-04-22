package com.fmisser.fpp.mq.rabbit.prop;

import lombok.Data;

/**
 * @author fmisser
 * @create 2021-04-13 下午2:20
 * @description 交换机其他参数配置
 */

@Data
public class RabbitExchangeArgsProperty {
    private Boolean delayed = false;
}
