package com.fmisser.gtc.base.dto.social;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 用户礼物消费
 */
public interface ConsumerGiftBillDto {
    String getConsumerDigitId();
    String getConsumerNick();
    String getAnchorDigitId();
    String getAnchorNick();
    BigDecimal getConsume();
    String getGiftName();
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    Date getCreateTime();
}
