package com.fmisser.gtc.social.domain;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

/**
 * 屏蔽信息
 */

@Entity
@Table(name = "t_block", indexes = {
        @Index(columnList = "createTime"),
        @Index(columnList = "userId")})
@Data
@EntityListeners(AuditingEntityListener.class)
@DynamicInsert
@DynamicUpdate
public class Block {
    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    // 屏蔽类型：
    // (保留)1: 屏蔽某个用户，对方不能查看你的资料、动态，对方无法给你发消息，通话；可以查看对方资料、动态，主动可以发消息，通话
    // (保留)2：拉黑某个用户，包含屏蔽的功能；同时也不能看对方的动态、资料，也无法主动发消息，通话
    // 10：不看他的动态
    // 11：不让他看自己的动态
    // 12：不看他的某一条动态
    // 20：加入黑名单，无法和对方聊天和通话
    @Column(nullable = false)
    private int type;

    @Column(nullable = false)
    private Long blockUserId;

    @Column
    private Long blockDynamicId;

    @Column(nullable = false, columnDefinition = "int default 1")
    private int block = 1;

    @CreatedDate
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column
    private Date createTime;
}
