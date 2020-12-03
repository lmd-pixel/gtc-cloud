package com.fmisser.gtc.base.dto.social;

import java.math.BigDecimal;
import java.util.Date;

public interface RechargeDto {
    String getOrderNumber();
    int getType();
    BigDecimal getCoin();
    BigDecimal getCoinBefore();
    Integer getStatus();
    Date getCreateTime();
    String getDigitId();
    String getNick();
    String getPhone();
}
