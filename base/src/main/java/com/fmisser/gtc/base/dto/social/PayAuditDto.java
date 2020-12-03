package com.fmisser.gtc.base.dto.social;

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
    Date getCreateTime();
}
