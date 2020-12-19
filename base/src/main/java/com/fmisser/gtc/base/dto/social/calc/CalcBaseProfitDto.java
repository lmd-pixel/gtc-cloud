package com.fmisser.gtc.base.dto.social.calc;

import java.math.BigDecimal;

public interface CalcBaseProfitDto {
    // 总数量
    Long getCount();

    // 总消费
    BigDecimal getConsume();

    // 总收益
    BigDecimal getProfit();

    // 总抽成
    BigDecimal getCommission();
}
