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
 * 用户购买的商品
 */

@Entity
@Table(name = "t_product")
@Data
@EntityListeners(AuditingEntityListener.class)
@DynamicInsert
@DynamicUpdate
public class Product {
    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 唯一标识一个产品的名称
    @Column(nullable = false, unique = true)
    private String name;

    @Column
    private String message;

    // 商品级别，用来排序等
    @Column(nullable = false, columnDefinition = "int default 0")
    private int level = 0;

    // 充值的币种 0: 人民币
    @Column(nullable = false)
    private int type;

    // 充值价格
    @Column(nullable = false)
    private BigDecimal price;

    // 充值获得的聊币
    @Column(nullable = false)
    private BigDecimal coin;

    @JsonIgnore
    // 充值的平台 0：苹果支付
    @Column(nullable = false)
    private int plt;

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
