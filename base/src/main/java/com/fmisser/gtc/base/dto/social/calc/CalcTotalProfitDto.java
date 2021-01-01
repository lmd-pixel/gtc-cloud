package com.fmisser.gtc.base.dto.social.calc;

import java.math.BigDecimal;

public interface CalcTotalProfitDto {
    // 获取平台收益
    BigDecimal getTotalCommission();

    // 获取主播收益
    BigDecimal getTotalProfit();

    // 获取总支出
    BigDecimal getTotalConsume();
}
