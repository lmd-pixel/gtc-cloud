package com.fmisser.gtc.base.dto.social;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 返回给前端的推荐主播
 */

@Data
@NoArgsConstructor
public class RecommendAnchorDto {
    public RecommendAnchorDto(Long id, String digitId, String nick, int gender, String head,
                              Date startTime, Date endTime, Date startTime2, Date endTime2) {
        this.id = id;
        this.digitId = digitId;
        this.nick = nick;
        this.gender = gender;
        this.head = head;
        this.startTime = startTime;
        this.endTime = endTime;
        this.startTime2 = startTime2;
        this.endTime2 = endTime2;
    }

    Long id;
    String digitId;
    String nick;
    int gender;
    @JsonIgnore
    String head;
    String headUrl;
    @JsonFormat(timezone = "GMT+8", pattern = "HH:mm:ss")
    Date startTime;
    @JsonFormat(timezone = "GMT+8", pattern = "HH:mm:ss")
    Date endTime;
    @JsonFormat(timezone = "GMT+8", pattern = "HH:mm:ss")
    Date startTime2;
    @JsonFormat(timezone = "GMT+8", pattern = "HH:mm:ss")
    Date endTime2;
}
