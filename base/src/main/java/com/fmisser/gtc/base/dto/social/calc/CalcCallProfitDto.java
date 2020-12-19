package com.fmisser.gtc.base.dto.social.calc;

import java.math.BigDecimal;

/**
 * 通话的收益的统计
 */

public interface CalcCallProfitDto extends CalcBaseProfitDto {
    // 总时长
    Long getDuration();
}
