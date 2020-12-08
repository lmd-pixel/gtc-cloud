package com.fmisser.gtc.base.dto.social;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 主播礼物收益结构
 */

public interface AnchorGiftBillDto {
    String getDigitId();
    String getNick();
    String getPhone();
    BigDecimal getProfit();
    String getGiftName();
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    Date getCreateTime();
}
