package com.fmisser.gtc.base.dto.social;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class UserCallDto {

    public UserCallDto(Long callId, String digitId, String nick, int type, int duration,
                       Date createTime, Date startTime, Date finishTime,
                       String head) {
        this.callId = callId;
        this.digitId = digitId;
        this.nick = nick;
        this.type = type;
        this.connected = 0L;
        this.duration = duration;
        this.createTime = createTime;
        this.startTime = startTime;
        this.finishTime = finishTime;
        this.head = head;
    }

    private Long callId;
    private String digitId;
    private String nick;
    private int type;
    private Long connected;
    private int duration;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date finishTime;
    @JsonIgnore
    private String head;
    private String headUrl;
    private String headThumbnailUrl;    // 头像缩略图
}
