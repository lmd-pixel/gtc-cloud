package com.fmisser.gtc.base.dto.social;

import java.math.BigDecimal;

/**
 * 通用收益统计数据
 */
public interface CommonBillDto {
    // 平台抽成
    public BigDecimal getCommission();
    // 主播收益
    public BigDecimal getProfit();
    // 用户消费
    public BigDecimal getOrigin();
    // 用户人数
    public BigDecimal getUsers();
}
