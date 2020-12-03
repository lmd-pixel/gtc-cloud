package com.fmisser.gtc.base.dto.social;

import java.math.BigDecimal;
import java.util.Date;

public interface AnchorMessageBillDto {
    String getDigitId();
    String getNick();
    String getPhone();
    BigDecimal getProfit();
    Date getCreateTime();
}
