package com.fmisser.gtc.base.dto.social;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author by fmisser
 * @create 2021/5/29 12:07 下午
 * @description TODO
 */

@Data
@NoArgsConstructor
public class GuardDto {
    public GuardDto(String digitId, String nick, int gender, Date birth, String head, long isGuard) {
        this.digitId = digitId;
        this.nick = nick;
        this.gender = gender;
        this.birth = birth;
        this.head = head;
        this.isGuard = isGuard;
    }


    String digitId;
    String nick;
    int gender;
    @JsonIgnore
    Date birth;
    @JsonIgnore
    String head;
    long isGuard;

    int age;
    private String headUrl;
    private String headThumbnailUrl;    // 头像缩略图
}
