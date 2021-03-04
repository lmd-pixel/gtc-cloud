package com.fmisser.gtc.base.dto.social.calc;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 通话总计
 */
public interface CalcTotalCallDto {
    // 拨打人数
    Long getCallUsers();

    // 拨打次数
    Long getCallTimes();

    // 接听人数
    Long getAcceptUsers();

    // 接通次数
    Long getAcceptTimes();

    // 接通总时长
    Long getDuration();

    // 视频卡数量
    Long getVideoCard();
}
