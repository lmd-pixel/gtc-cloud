package com.fmisser.gtc.social.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 语音，视频通话账单
 */
@Entity
@Table(name = "t_call_bill",
        indexes = {@Index(columnList = "userIdFrom,creatTime"), @Index(columnList = "userIdTo,creatTime")})
@Data
@EntityListeners(AuditingEntityListener.class)
@DynamicInsert
@DynamicUpdate
public class CallBill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 流水号
    @Column(nullable = false, unique = true)
    private String serialNumber;

    @Column(nullable = false)
    private Long userIdFrom;

    @Column(nullable = false)
    private Long userIdTo;

    //
    @Column(nullable = false)
    private String callId;

    // 类型 0： 语音， 1： 视频
    @Column(nullable = false)
    private int type;

    // 消费来源, 0: 金币消费
    @Column(nullable = false, columnDefinition = "int default 0")
    private int source = 0;

    // 创建消费的阶段，从通话开始的每一个分钟内为一个阶段， 第一分钟内为0
    @Column(nullable = false, columnDefinition = "int default 0")
    private int stage = 0;

    // 用户原始消费币
    @Column(nullable = false)
    private BigDecimal originCoin;

    // 平台抽成比例
    @Column(nullable = false)
    private BigDecimal commissionRatio;

    // 平台抽成币
    @Column(nullable = false)
    private BigDecimal commissionCoin;

    // 主播收益币
    @Column(nullable = false)
    private BigDecimal profitCoin;

    // 是否有效，如果产生争议，考虑退回，默认有效，并已消费
    @Column(nullable = false, columnDefinition = "int default 1")
    private int valid = 1;

    @Column
    private String remark;

    @CreatedDate
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column
    private Date creatTime;

    @LastModifiedDate
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column
    private Date modifyTime;

    @CreatedBy
    @Column
    private String createBy;

    @LastModifiedBy
    @Column
    private String modifyBy;
}
