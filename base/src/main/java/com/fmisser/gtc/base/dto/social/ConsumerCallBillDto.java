package com.fmisser.gtc.base.dto.social;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 用户通话消费
 */
public interface ConsumerCallBillDto {
    String getConsumerDigitId();
    String getConsumerNick();
    String getAnchorDigitId();
    String getAnchorNick();
    BigDecimal getConsume();
    Integer getDuration();
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    Date getCreateTime();
}
