package com.fmisser.gtc.auth.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

/**
 * 极光短信验证码
 * 请求验证码的信息
 */
@Entity
@Table(name = "t_phone_code_request", indexes = {@Index(columnList = "phone,type,createTime")})
@Data
@EntityListeners(AuditingEntityListener.class)
public class PhoneCodeRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private String msgId;

    @Column(nullable = false)
    private int type;

    @CreatedDate
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column
    private Date createTime;
}
