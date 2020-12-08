package com.fmisser.gtc.social.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

/**
 * 苹果支付票据数据
 */

@Entity
@Table(name = "t_iap_receipt")
@Data
@EntityListeners(AuditingEntityListener.class)
@DynamicInsert
@DynamicUpdate
public class IapReceipt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 支付 id, 有则关联
    @Column
    private Long rechargeId;

    // 用户 id, 有则关联
    @Column
    private Long userId;

    // 环境 0: 沙盒 1： 正式
    @Column
    private int env;

    // 返回的票据结果
    @Column
    private int status;

    @CreatedDate
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column
    private Date createTime;

    // 以下为iap receipt 数据

    @Column
    private int quantity;

    @Column(name = "product_id")
    private String productId;

    @Column(name = "transaction_id")
    private String transactionId;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "purchase_date")
    private Date purchaseDate;

    @Column(name = "original_transaction_id")
    private String originalTransactionId;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "original_purchase_date")
    private Date originalPurchaseDate;

    @Column(name = "app_item_id")
    private String appItemId;

    @Column(name = "version_external_identifier")
    private String versionExternalIdentifier;

    @Column
    private String bid;

    @Column
    private String bvrs;
}
