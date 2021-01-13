package com.fmisser.gtc.auth.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

/**
 * 极光一键登录认证信息
 */
@Entity
@Table(name = "t_phone_token_request", indexes = {@Index(columnList = "phone,createTime")})
@Data
@EntityListeners(AuditingEntityListener.class)
public class PhoneTokenRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @Column(nullable = false)
    @Column
    private String phone;

    // 认证的token
    @Column(nullable = false)
    private String token;

    // 请求的唯一id
    @Column(name = "request_id")
    private Long requestId;

    // 附加字段
    @Column(name = "ex_id")
    private String exId;

    // 认证结果code
    @Column
    private int code;

    // 认证结果说明
    @Column
    private String content;

    // 加密的认证手机号
    @Column(name = "encode_phone")
    private String encodePhone;

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
}
