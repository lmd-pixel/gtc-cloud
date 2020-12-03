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
 * 提现审核
 */

@Entity
@Table(name = "t_withdraw_audit",
        indexes = {@Index(columnList = "userId"), @Index(columnList = "createTime")})
@Data
@EntityListeners(AuditingEntityListener.class)
@DynamicInsert
@DynamicUpdate
public class WithdrawAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 订单号
    @Column(nullable = false, unique = true)
    private String orderNumber;

    @Column(nullable = false)
    private Long userId;

    // 当前提取
    @Column
    private BigDecimal drawCurr;

    // 最大提取
    @Column
    private BigDecimal drawMax;

    // 实际获得
    @Column
    private BigDecimal drawActual;

    // 提现前币
    @Column
    private BigDecimal coinBefore;

    // 提现后币
    @Column
    private BigDecimal coinAfter;

    // 手续扣费
    @Column
    private BigDecimal fee;

    // 手续比例
    @Column
    private BigDecimal feeRatio;

    // 打款币种 0:人命币 默认
    @Column
    private int moneyType;

    // 应打款金额
    @Column
    private BigDecimal payMoney;

    // 实际打款金额
    @Column
    private BigDecimal payActual;

    // 打款信息为快照方式
    // 打款账户类型 0: 支付宝 1：银行卡
    @Column
    private int payType;

    // 收款人
    @Column
    private String payToPeople;

    // 收款账户
    @Column
    private String payToAccount;

    /**
     * 0: 未审核
     * 10：审核中
     * 20: 审核未通过
     * 21：审核取消
     * 22: 审核冻结
     * 30：审核通过，待打款
     * 40：打款完成
     * 41：打款失败
     * 42: 部分打款
     * 43：打款冻结
     */
    @Column(nullable = false)
    private int status;

    @Column
    private String remark;

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

    @CreatedBy
    @Column
    private String createBy;

    @LastModifiedBy
    @Column
    private String modifyBy;
}
