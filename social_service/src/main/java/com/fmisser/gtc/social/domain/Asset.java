package com.fmisser.gtc.social.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * 用户的个人资产信息
 */

@Entity
@Table(name = "t_asset")
@Data
public class Asset {

//    @OneToOne(fetch = FetchType.LAZY, optional = false)
//    @JoinColumn(name = "user_id", referencedColumnName = "id")
//    private User user;

    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @Column(nullable = false, unique = true)
    private Long userId;

    // 用户的聊币
    @Column(nullable = false)
    private BigDecimal coin = BigDecimal.ZERO;

    @Column(nullable = false, columnDefinition = "int default 0")
    private int freeDialDuration = 0;

    @Column(nullable = false, columnDefinition = "int default 0")
    private int freeAnswerDuration = 0;

    // 用户的vip等级
    @Column(name = "vip_level", nullable = false, columnDefinition = "int default 0")
    private int vipLevel = 0;

    // 聊天抵用券
    @Column(nullable = false, columnDefinition = "int default 0")
    private int msgFreeCoupon = 0;

    // TODO: 2020/12/30 优惠券等信息考虑以后放到另外一个table，后期种类很多同时需要考虑一些其他因素比如过期时间
    // 一分钟语音抵用券
    @Column(nullable = false, columnDefinition = "int default 0")
    private int voiceFreeCoupon1 = 0;

    // 用户的消息收益比例
    @Column(nullable = false, columnDefinition = "decimal(6,2) default 0.50")
    private BigDecimal msgProfitRatio = BigDecimal.valueOf(0.50);

    // 礼物收益比例
    @Column(nullable = false, columnDefinition = "decimal(6,2) default 0.50")
    private BigDecimal giftProfitRatio = BigDecimal.valueOf(0.50);

    // 语音通话收益比例
    @Column(nullable = false, columnDefinition = "decimal(6,2) default 0.60")
    private BigDecimal voiceProfitRatio = BigDecimal.valueOf(0.60);

    // 视频通话收益比例
    @Column(nullable = false, columnDefinition = "decimal(6,2) default 0.60")
    private BigDecimal videoProfitRatio = BigDecimal.valueOf(0.60);

    @JsonIgnore
    @Version
    private Long version;
}
