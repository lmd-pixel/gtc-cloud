package com.fmisser.gtc.social.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

/**
 * 动态互动,评论点赞等
 */
@Entity
@Table(name = "t_interact", indexes = {@Index(columnList = "dynamicId,createTime")})
@Data
@EntityListeners(AuditingEntityListener.class)
public class Interact {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long dynamicId;

    @Column(nullable = false)
    private Long youngId;

    /**
     * 类型 0： 点赞  1： 评论
     */
    @Column(nullable = false)
    private int type;

    @Column
    private String content;

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

    @Column(nullable = false, columnDefinition = "int default 0")
    private int isDelete = 0;
}
