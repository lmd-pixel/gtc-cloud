package com.fmisser.gtc.base.dto.social;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 主播数据,包括收益统计
 */
public interface AnchorDto {
    String getDigitId();
    String getNick();
    String getPhone();
    int getGender();
    Long getFollows();
    BigDecimal getAudioDuration();
    BigDecimal getVideoDuration();
    BigDecimal getAudioProfit();
    BigDecimal getVideoProfit();
    BigDecimal getMessageProfit();
    BigDecimal getGiftProfit();
    BigDecimal getCoin();
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    Date getCreateTime();
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    Date getActiveTime();
}
