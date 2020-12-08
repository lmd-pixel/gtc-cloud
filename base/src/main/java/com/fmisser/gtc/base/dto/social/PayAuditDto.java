package com.fmisser.gtc.base.dto.social;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 打款审核数据
 */
public interface PayAuditDto {
    String getDigitId();
    String getNick();
    String getOrderNumber();
    BigDecimal getPayActual();
    Integer getPayType();
    String getPayToPeople();
    String getPayToAccount();
    String getRemark();
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    Date getCreateTime();
}
