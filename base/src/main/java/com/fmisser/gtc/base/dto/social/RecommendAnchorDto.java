package com.fmisser.gtc.base.dto.social;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 返回给前端的推荐主播
 */

@Data
@NoArgsConstructor
public class RecommendAnchorDto {
    public RecommendAnchorDto(Long id, String digitId, String nick, int gender, String head) {
        this.id = id;
        this.digitId = digitId;
        this.nick = nick;
        this.gender = gender;
        this.head = head;
    }

    Long id;
    String digitId;
    String nick;
    int gender;
    @JsonIgnore
    String head;
    String headUrl;
}
