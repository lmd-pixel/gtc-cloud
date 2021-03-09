package com.fmisser.gtc.base.dto.social.calc;

import java.math.BigDecimal;

/**
 * 统计主播收益
 */
public interface CalcAnchorProfitDto {
    BigDecimal getTotalProfit();
    BigDecimal getVideoProfit();
    BigDecimal getVoiceProfit();
    BigDecimal getMsgProfit();
    BigDecimal getGiftProfit();
}
