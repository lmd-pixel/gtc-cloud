package com.fmisser.gtc.base.dto.social;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 动态返回的数据
 */
@Data
public class DynamicDto {

    public DynamicDto(String content) {

    }

    public DynamicDto(Long id, Long userId, String digitId,int identity, String content, String city,
                      int type, String video, String pictures,
                      Date createTime, Date modifyTime, BigDecimal longitude, BigDecimal latitude,
                      Long heartCount, Long commentCount,
                      String nick, Date birth, int gender, String head, String message, int status) {

        this.id = id;
        this.userId = userId;
        this.digitId = digitId;
        this.identity = identity;
        this.content = content;
        this.city = city;
        this.type = type;
        this.video = video;
        this.pictures = pictures;
        this.createTime = createTime;
        this.modifyTime = modifyTime;
        this.longitude = longitude;
        this.latitude = latitude;
        this.heartCount = heartCount;
        this.selfHeart = 0L;
        this.commentCount = commentCount;
        this.follow = 0L;
        this.nick = nick;
        this.birth = birth;
        this.gender = gender;
        this.head = head;
        this.selfIsGuard = 0L;
        this.message = message;
        this.status = status;
    }

    public DynamicDto(Long id, Long userId, String digitId, String content, String city,
                      int type, String video, String pictures,
                      Date createTime, Date modifyTime, BigDecimal longitude, BigDecimal latitude,
                      Long heartCount, Long selfHeart, Long commentCount,
                      String nick, Date birth, int gender, String head) {
        this.id = id;
        this.userId = userId;
        this.digitId = digitId;
        this.content = content;
        this.city = city;
        this.type = type;
        this.video = video;
        this.pictures = pictures;
        this.createTime = createTime;
        this.modifyTime = modifyTime;
        this.longitude = longitude;
        this.latitude = latitude;
        this.heartCount = heartCount;
        this.selfHeart = selfHeart;
        this.commentCount = commentCount;
        this.follow = 1L;   // 表示已关注
        this.nick = nick;
        this.birth = birth;
        this.gender = gender;
        this.head = head;
        this.selfIsGuard = 0L;
    }

    public DynamicDto(Long id, Long userId, String digitId,int identity, String content, String city,
                      int type, String video, String pictures,
                      Date createTime, Date modifyTime, BigDecimal longitude, BigDecimal latitude,
                      Long heartCount, Long selfHeart, Long commentCount, Long follow,
                      String nick, Date birth, int gender, String head) {
        this.id = id;
        this.userId = userId;
        this.digitId = digitId;
        this.identity=identity;
        this.content = content;
        this.city = city;
        this.type = type;
        this.video = video;
        this.pictures = pictures;
        this.createTime = createTime;
        this.modifyTime = modifyTime;
        this.longitude = longitude;
        this.latitude = latitude;
        this.heartCount = heartCount;
        this.selfHeart = selfHeart;
        this.commentCount = commentCount;
        this.follow = follow;
        this.nick = nick;
        this.birth = birth;
        this.gender = gender;
        this.head = head;
        this.selfIsGuard = 0L;
    }

    private Long id;
    private Long userId;
    private String digitId;
    private int identity;
    private String content;
    private String city;
    private int type;
    @JsonIgnore
    private String video;
    @JsonIgnore
    private String pictures;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date modifyTime;
    private BigDecimal longitude;
    private BigDecimal latitude;
    private Long heartCount;
    private Long selfHeart;
    private Long commentCount;
    private Long follow;
    private List<String> pictureUrlList; // 图片列表
    private List<String> pictureThumbnailUrlList; // 图片缩略图列表
    private String videoUrl;    // 视频
    private String videoThumbnailUrl;
    private String nick;
    @JsonIgnore
    private Date birth;
    private String constellation;   // 星座
    private int age; // 年龄
    private int gender;
    @JsonIgnore
    private String head;
    private String headUrl;
    private String headThumbnailUrl;    // 头像缩略图
    private Long selfIsGuard;
    private String message;
    private int status;
}

