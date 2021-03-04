package com.fmisser.gtc.base.dto.social;

import java.math.BigDecimal;

public interface CallDetailDto extends CallDto {
    BigDecimal getConsume();
    BigDecimal getProfit();
    Integer getFreeCard();
    BigDecimal getVideoPrice();
    BigDecimal getAudioPrice();
    BigDecimal getVideoRatio();
    BigDecimal getAudioRatio();
}
