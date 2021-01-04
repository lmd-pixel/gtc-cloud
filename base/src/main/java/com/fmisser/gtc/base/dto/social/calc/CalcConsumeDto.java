package com.fmisser.gtc.base.dto.social.calc;

import java.math.BigDecimal;

/**
 * 用户消费和充值统计
 */
public interface CalcConsumeDto {
    // 总数量
    Long getCount();

    // 总充值
    BigDecimal getRecharge();

    // 视频通话总消费
    BigDecimal getVideoConsume();

    // 语音通话总消费
    BigDecimal getVoiceConsume();

    // 消息消费
    BigDecimal getMsgConsume();

    // 礼物消费
    BigDecimal getGiftConsume();
}
