package com.fmisser.gtc.base.dto.social;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 提现审核数据
 */
public interface WithdrawAuditDto {
    String getDigitId();
    String getNick();
    String getOrderNumber();
    BigDecimal getDrawActual();
    BigDecimal getCoinAfter();
    Integer getStatus();
    String getRemark();
    Date getCreateTime();
}
