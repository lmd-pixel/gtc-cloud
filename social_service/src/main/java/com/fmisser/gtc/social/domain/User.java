package com.fmisser.gtc.social.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 个人基础信息
 */

@Entity
@Table(name = "t_user", indexes = {@Index(columnList = "createTime")})
@Data
@EntityListeners(AuditingEntityListener.class)
@DynamicInsert
@DynamicUpdate
public class User implements Serializable {
//    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String digitId;

    @JsonIgnore
    // 目前 username 就是 phone
    @Column(nullable = false, unique = true)
    private String username;

    @Column
    private String phone;

    @Column
    private String nick;

    @JsonIgnore
    @Column
    private String head;

//    @JsonIgnore
    @Column
//    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date birth;

    // 男女
    @Column
    private int gender;

    // 城市
    @Column
    private String city;

    // 职业
    @Column
    private String profession;

    // 个性签名
    @Column
    private String intro;

    // 语音介绍
    @JsonIgnore
    @Column
    private String voice;

    // 保留字段，暂时不用
    @JsonIgnore
    @Column
    private String loves;

    /**
     * 上传的照片,多张照片逗号隔开
     */
    @Column(length = 4096)
    private String photos;

    @JsonIgnore
    // 自拍认证图片
    @Column
    private String selfie;

    @JsonIgnore
    // 视频资料
    @Column
    private String video;

    // 语音呼叫价格
    @Column
    private BigDecimal callPrice;

    // 视频呼叫价格
    @Column
    private BigDecimal videoPrice;

    // 单条聊天价格
    @Column
    private BigDecimal messagePrice;

    // 身份 0：普通用户 1： 主播
    @Column(nullable = false, columnDefinition = "int default 0")
    private int identity = 0;

    // 模式 0: 音视频 1: 音频 2: 视频
    @Column(nullable = false, columnDefinition = "int default 0")
    private int mode = 0;

    // 休息状态 0： 不休息  1： 休息
    @Column(nullable = false, columnDefinition = "int default 0")
    private int rest = 0;

    // 休息开始时间
    @JsonFormat(timezone = "GMT+8", pattern = "HH:mm")
    @DateTimeFormat(pattern = "HH:mm")
    @Column
    private Date restStartDate;

    // 休息停止时间
    @JsonFormat(timezone = "GMT+8", pattern = "HH:mm")
    @DateTimeFormat(pattern = "HH:mm")
    @Column
    private Date restEndDate;

    // 是否已经真人视频认证
    @Column(nullable = false, columnDefinition = "int default 0")
    private int videoAudit = 0;

    // 关注数量
    @Column(nullable = false, columnDefinition = "int default 0")
    private int follows = 0;

    @CreatedDate
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column
    private Date createTime;

    @LastModifiedDate
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column
    private Date modifyTime;

//    // 资产信息
//    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
//    private Asset asset;

//    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
//    @Transient
//    private VerifyStatus verifyStatus;

    // 标签信息
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(
            name = "t_user_label",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "label_id", referencedColumnName = "id"))
    private List<Label> labels;

    @JsonIgnore
    @Version
    private Long version;

    @Transient
    private String constellation;   // 星座

    @Transient
    private int age; // 年龄

    @Transient
    private List<String> photoUrlList; // 照片列表

    @Transient
    private List<String> photoThumbnailUrlList; // 照片缩略图列表

    @Transient
    private String headUrl;

    @Transient
    private String headThumbnailUrl;    // 头像缩略图

    @JsonIgnore
    @Transient
    private String selfieUrl;

    @JsonIgnore
    @Transient
    private String selfieThumbnailUrl;  // 自拍缩略图

    @Transient
    private String voiceUrl;

    @Transient
    private String videoUrl;

    @Transient
    private BigDecimal coin;

    @Transient
    private BigDecimal voiceProfitRatio;

    @Transient
    private BigDecimal videoProfitRatio;

    @Transient
    private BigDecimal msgProfitRatio;

    @Transient
    private BigDecimal giftProfitRatio;

    // 是否被别人屏蔽动态
    @Transient
    private int blockDynamic;

    // 是否被别人屏蔽聊天
    @Transient
    private int blockChat;

    // 是否被别人关注
    @Transient
    private int isFollow;

    @Transient
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date birthDay;

    // 当前时间是否休息
    @Transient
    private int currRest;

    // 守护版本 照片
    @Transient
    private List<UserMaterial> originPhotos;

    // 守护版本
    @Transient
    private List<UserMaterial> thumbnailPhotos;

    // 守护版本 视频认证url
    @Transient
    private String videoAuditUrl;

    // 守护版本 是否是我守护的主播
    @Transient
    private int isGuard;
}
