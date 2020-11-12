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
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String digitId;

    // 目前 username 就是 phone
    @Column(nullable = false, unique = true)
    private String username;

    @Column
    private String phone;

    @Column
    private String nick;

    @Column
    private String head;

    @JsonIgnore
    @Column
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
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
    @Column
    private String voice;

    // 保留字段，暂时不用
    @JsonIgnore
    @Column
    private String loves;

    /**
     * 上传的照片,多张照片逗号隔开
     */
    @JsonIgnore
    @Column
    private String photos;

    // 自拍认证图片
    @Column
    private String selfie;

    // 语音呼叫价格
    @Column
    private BigDecimal callPrice;

    // 视频呼叫价格
    @Column
    private BigDecimal videoPrice;

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

    // 资产信息
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private Asset asset;

    // 资产信息
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private VerifyStatus verifyStatus;

    // 标签信息
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(
            name = "t_user_label",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "label_id", referencedColumnName = "id"))
    private List<Label> labels;

    @Transient
    private String constellation;   // 星座

    @Transient
    private int age; // 年龄

    @Transient
    private List<String> photoList; // 照片列表
}
