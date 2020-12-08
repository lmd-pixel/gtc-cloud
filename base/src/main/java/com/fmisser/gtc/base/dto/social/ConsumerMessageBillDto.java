package com.fmisser.gtc.base.dto.social;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 用户私信消费
 */
public interface ConsumerMessageBillDto {
    String getConsumerDigitId();
    String getConsumerNick();
    String getAnchorDigitId();
    String getAnchorNick();
    BigDecimal getConsume();
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    Date getCreateTime();
}
