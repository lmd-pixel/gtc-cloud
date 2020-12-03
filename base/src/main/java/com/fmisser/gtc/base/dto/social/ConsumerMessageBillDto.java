package com.fmisser.gtc.base.dto.social;

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
    Date getCreateTime();
}
