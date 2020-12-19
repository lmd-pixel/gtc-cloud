package com.fmisser.gtc.social.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
 * 用户用游戏内资产购买的消费品
 */

@Entity
@Table(name = "t_goods")
@Data
@EntityListeners(AuditingEntityListener.class)
@DynamicInsert
@DynamicUpdate
public class Goods {
    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 唯一标识一个消费品的名称
    @Column(nullable = false, unique = true)
    private String name;

    @Column
    private String message;

    // 消费品级别，用来排序等
    @Column(nullable = false, columnDefinition = "int default 0")
    private int level = 0;

    // 消费品类型 0： 使用聊币
    @Column(nullable = false)
    private int type;

    // 购买的价格
    @Column(nullable = false)
    private BigDecimal price;

    // 购买限制 0: 不限制， 其他按数量多少限制
    @Column(name = "limit_count", nullable = false)
    private int limitCount;

    // 限制周期 0: 无期限 1：每天（0点刷新库存） 2：每天（5点刷新库存） 3：每周（0点刷新） 4：每月
    @Column(nullable = false, name = "limit_type")
    private int limitType;

    // 有效期开始
    @Column
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date validBegin;

    // 有效期结束
    @Column
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date validEnd;

    @Column(nullable = false, columnDefinition = "int default 0")
    private int disable = 0;

    @JsonIgnore
    @CreatedDate
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column
    private Date creatTime;

    @JsonIgnore
    @LastModifiedDate
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column
    private Date modifyTime;

    @JsonIgnore
    @CreatedBy
    @Column
    private String createBy;

    @JsonIgnore
    @LastModifiedBy
    @Column
    private String modifyBy;
}
