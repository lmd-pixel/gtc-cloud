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
 * 身份审核
 */

@Entity
@Table(name = "t_identity_audit",
        indexes = {@Index(columnList = "userId"), @Index(columnList = "createTime")})
@Data
@EntityListeners(AuditingEntityListener.class)
@DynamicInsert
@DynamicUpdate
public class IdentityAudit {
    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @Column(nullable = false)
    private Long userId;

    // 编号
    @Column(nullable = false, unique = true)
    private String serialNumber;

    @Column
    private String digitId;

    @Column
    private String phone;

    @Column
    private Integer gender;

    @Column
    private Integer age;

    @Column
    private String nick;

    @Column
    private Date birth;

    @Column
    private String city;

    @Column
    private String profession;

    @Column
    private String intro;

    @Column
    private String labels;

    @Column
    private BigDecimal callPrice;

    @Column
    private BigDecimal videoPrice;

    @Column
    private BigDecimal messagePrice;

    @Column
    private String voice;

    @Column
    private String head;

    @Column(length = 4096)
    private String photos;

    @Column
    private String video;

    /**
     * 审核类型 1： 资料审核  2： 相册审核  3：视频审核 11：待审资料 12：待审相册 13：待审视频
     */
    @Column(nullable = false)
    private Integer type;

    /**
     * 10： 审核中 20: 审核未通过 30： 审核通过
     */
    @Column(nullable = false)
    private Integer status;

    @Column
    private String message;

    @CreatedDate
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column
    private Date createTime;

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
