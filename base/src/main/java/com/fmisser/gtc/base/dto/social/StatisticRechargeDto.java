package com.fmisser.gtc.base.dto.social;

import java.math.BigDecimal;

/**
 * 支付数据统计
 */

public interface StatisticRechargeDto {
    // 支付人数
    Long getUsers();
    // 支付总金额
    BigDecimal getPay();
    // 平台总收入
    BigDecimal getIncome();
}
