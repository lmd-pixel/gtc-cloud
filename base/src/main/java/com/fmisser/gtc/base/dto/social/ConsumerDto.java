package com.fmisser.gtc.base.dto.social;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 用户数据
 */
public interface ConsumerDto {
    String getDigitId();
    String getNick();
    String getPhone();
    BigDecimal getRechargeCoin();
    BigDecimal getAudioCoin();
    BigDecimal getVideoCoin();
    BigDecimal getMessageCoin();
    BigDecimal getGiftCoin();
    String getChannelId();
    BigDecimal getCoin();
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    Date getCreateTime();
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    Date getActiveTime();
}
