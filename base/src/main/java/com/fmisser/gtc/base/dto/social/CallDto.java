package com.fmisser.gtc.base.dto.social;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

// 通话数据结构
public interface CallDto {
    // 通话唯一id
    Long getCallId();

    // 发起人数字id
    String getCallDigitId();

    // 发起人昵称
    String getCallNick();

    // 接听人数字id
    String getAcceptDigitId();

    // 接听人昵称
    String getAcceptNick();

    // 0 音频， 1视频
    Integer getType();

    // 0 未接通， 1接通
    Integer getConnected();

    /**
     * 0：用户给主播拨打
     * 1：主播给用户拨打, 拨打人和用户反过来
     * 3：用户给用户拨打, 暂时不允许
     * 4：主播给主播拨打
     */
    Integer getMode();

    // 总时长，如果是0表示没接通
    Integer getDuration();

    // 音视频卡
    Integer getFreeCard();

    // 开始时间
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    Date getStartTime();

    // 结束时间
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    Date getEndTime();
}
