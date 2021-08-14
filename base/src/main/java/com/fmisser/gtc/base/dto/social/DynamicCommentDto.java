package com.fmisser.gtc.base.dto.social;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class DynamicCommentDto {

    public DynamicCommentDto(Long id, Long dynamicId, Long commentIdTo,
                             Long userIdFrom, Long userIdTo,
                             String userDigitIdFrom, String userDigitIdTo,
                             String content, Date createTime,
                             String nickFrom, String nickTo, String head, int gender) {
        this.id = id;
        this.dynamicId = dynamicId;
        this.commentIdTo = commentIdTo;
        this.userIdFrom = userIdFrom;
        this.userIdTo = userIdTo;
        this.userDigitIdFrom = userDigitIdFrom;
        this.userDigitIdTo = userDigitIdTo;
        this.content = content;
        this.createTime = createTime;
        this.nickFrom = nickFrom;
        this.nickTo = nickTo;
        this.head = head;
        this.gender = gender;
    }

    public DynamicCommentDto(Long id, Long dynamicId, Long commentIdTo,
                             Long userIdFrom, Long userIdTo,
                             String content, Date createTime
                           ) {
        this.id = id;
        this.dynamicId = dynamicId;
        this.commentIdTo = commentIdTo;
        this.userIdFrom = userIdFrom;
        this.userIdTo = userIdTo;
        this.content = content;
        this.createTime = createTime;

    }

    private Long id;
    private Long dynamicId;
    private Long commentIdTo;
    private Long userIdFrom;
    private Long userIdTo;
    private String userDigitIdFrom;
    private String userDigitIdTo;
    private String content;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    private String nickFrom;
    private String nickTo;
    @JsonIgnore
    private String head;
    private String headUrl;
    private String headThumbnailUrl;    // 头像缩略图
    private Long isSelf;
    private int gender;
}
