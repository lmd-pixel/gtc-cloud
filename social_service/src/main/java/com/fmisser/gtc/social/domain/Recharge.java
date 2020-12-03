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

@Entity
@Table(name = "t_recharge",
        indexes = {@Index(columnList = "userId"),@Index(columnList = "orderNumber")})
@Data
@EntityListeners(AuditingEntityListener.class)
@DynamicInsert
@DynamicUpdate
public class Recharge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 订单号
    @Column(nullable = false, unique = true)
    private String orderNumber;

    @Column(nullable = false)
    private Long userId;

    // 购买的商品id
    @Column
    private Long productId;

    // 商品具体充值币
    @Column
    private BigDecimal coin;

    // 商品充值价格
    @Column
    private BigDecimal price;

    // 实际付款
    @Column
    private BigDecimal pay;

    // 实际平台收入
    @Column
    private BigDecimal income;

    // 充值前币
    @Column
    private BigDecimal coinBefore;

    // 充值后币
    @Column
    private BigDecimal coinAfter;

    /**
     * 充值状态
     * 1: 充值中
     * 10: 充值失败
     * 11: 取消充值
     * 20: 充值完成
     * 30: 全部退款
     * 31: 部分退款
     */
    @Column
    private int status;

    /**
     * 充值平台
     */
    @Column
    private int type;

    @Column
    private String remark;

    @CreatedDate
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column
    private Date creatTime;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column
    private Date finishTime;

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
