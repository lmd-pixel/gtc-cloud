package com.fmisser.gtc.base.dto.social.calc;

import java.math.BigDecimal;

public interface CalcConsumerProfitDto {
    BigDecimal getTotalProfit();
    BigDecimal getVideoProfit();
    BigDecimal getVoiceProfit();
    BigDecimal getMsgProfit();
    BigDecimal getGiftProfit();
}
