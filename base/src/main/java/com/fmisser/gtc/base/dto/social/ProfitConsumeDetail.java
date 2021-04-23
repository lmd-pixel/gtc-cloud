package com.fmisser.gtc.base.dto.social;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author fmisser
 * @create 2021-04-23 下午2:46
 * @description 收益消费详情
 */
public interface ProfitConsumeDetail {
    // 1 语音收益 2 视频收益 3 视频卡收益 4 礼物收益 5 语音消费 6 视频消费 7 视频卡消费 8 礼物消费
    Long getType();
    BigDecimal getVal();
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    Date getTime();
}
