package com.fmisser.gtc.base.dto.social;

import com.fasterxml.jackson.annotation.JsonFormat;

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
    BigDecimal getDrawCurr();
    BigDecimal getDrawMax();
    BigDecimal getFee();
    BigDecimal getFeeRatio();
    BigDecimal getCoinAfter();
    Integer getStatus();
    String getRemark();
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    Date getCreateTime();
}
