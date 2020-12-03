package com.fmisser.gtc.base.dto.social;

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
    Date getCreateTime();
}
