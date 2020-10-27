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

/**
 * 个人基础信息
 */

@Entity
@Table(name = "t_young")
@Data
@EntityListeners(AuditingEntityListener.class)
@DynamicInsert
@DynamicUpdate
public class Young {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long authId;

    @Column(nullable = false, unique = true)
    private String digitId;

    @Column(nullable = false, unique = true)
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

    @Column
    private int gender;

    @Column
    private String city;

    @Column
    private String profession;

    @Column
    private String intro;

    @Column
    private String voice;

    @Column
    private String labels;

    @Column
    private String loves;

    @Column
    private String photos;

    @Column
    private String verifyImage;

    /**
     * 认证状态 0: 未认证 10： 审核中 20: 审核未通过 30： 审核通过，已认证
     */
    @Column(nullable = false, columnDefinition = "int default 0")
    private int verifyStatus = 0;

    @Column(nullable = false)
    private BigDecimal coin = BigDecimal.ZERO;

    @Column
    private BigDecimal callPrice;

    @Column
    private BigDecimal videoPrice;

    @Column
    private int freeDialDuration;

    @Column
    private int freeAnswerDuration;

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

    @Transient
    private String constellation;   // 星座

    @Transient
    private String age; // 年龄
}
