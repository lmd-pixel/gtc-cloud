package com.fmisser.gtc.base.dto.social.calc;

import java.math.BigDecimal;

/**
 * 基础收益数据
 */
public interface CalcBaseProfitDto {
    // 总数量
    Long getCount();

    // 总消费
    BigDecimal getConsume();

    // 总收益
    BigDecimal getProfit();

    // 总抽成
    BigDecimal getCommission();

    // 总消费人数
    Long users();

    // 总主播人数
    Long anchors();
}
