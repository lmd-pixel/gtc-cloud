package com.fmisser.gtc.social.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;

/**
 * 用户资料状态
 */

//@Entity
//@Table(name = "t_verify_status")
//@Data
public class VerifyStatus {

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 资料是否完善 0: 未完善, 10: 已完善
    @Column(nullable = false, columnDefinition = "int default 0")
    private int profileStatus = 0;

    // 资料审核状态 0: 未审核 10： 审核中 20: 审核未通过 30： 审核通过，已认证
    @Column(nullable = false, columnDefinition = "int default 0")
    private int profileAuditStatus = 0;

    //  个人照片状态
    @Column(nullable = false, columnDefinition = "int default 0")
    private int photosStatus = 0;

    // 个人照片审核状态
    @Column(nullable = false, columnDefinition = "int default 0")
    private int photosAuditStatus;

    // 自拍状态
    @Column(nullable = false, columnDefinition = "int default 0")
    private int selfieStatus;

    @Column(nullable = false, columnDefinition = "int default 0")
    private int selfieAuditStatus;

    // 实名状态
    @Column(nullable = false, columnDefinition = "int default 0")
    private int realNameStatus;

    @Column(nullable = false, columnDefinition = "int default 0")
    private int realNameAuditStatus;

}
