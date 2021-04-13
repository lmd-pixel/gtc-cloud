package com.fmisser.fpp.mq.rabbit.prop;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author fmisser
 * @create 2021-04-13 下午2:20
 * @description
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RabbitExchangeArgsProperty {
    private Boolean delayed = false;
}
