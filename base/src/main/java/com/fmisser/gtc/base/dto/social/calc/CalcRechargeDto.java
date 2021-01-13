package com.fmisser.gtc.base.dto.social.calc;

import java.math.BigDecimal;

/**
 * 充值统计
 */
public interface CalcRechargeDto {
    // 总数量
    Long getCount();

    // 总充值
    BigDecimal getRecharge();
}
