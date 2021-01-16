package com.fmisser.gtc.base.dto.social;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 关注的人
 */
@Data
@NoArgsConstructor
public class ConcernDto {

    public ConcernDto(String digitId, String nick, int gender, Date birth, String head, long isFollow) {
        this.digitId = digitId;
        this.nick = nick;
        this.gender = gender;
        this.birth = birth;
        this.head = head;
        this.isFollow = isFollow;
    }

    String digitId;
    String nick;
    int gender;
    @JsonIgnore
    Date birth;
    @JsonIgnore
    String head;
    long isFollow;

    int age;
    private String headUrl;
    private String headThumbnailUrl;    // 头像缩略图
}
