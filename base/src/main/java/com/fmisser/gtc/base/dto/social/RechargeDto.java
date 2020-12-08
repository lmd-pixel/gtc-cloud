package com.fmisser.gtc.base.dto.social;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.util.Date;

public interface RechargeDto {
    String getOrderNumber();
    int getType();
    BigDecimal getCoin();
    BigDecimal getCoinBefore();
    Integer getStatus();
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    Date getCreateTime();
    String getDigitId();
    String getNick();
    String getPhone();
}
