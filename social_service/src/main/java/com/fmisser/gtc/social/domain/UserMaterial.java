package com.fmisser.gtc.social.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author by fmisser
 * @create 2021/5/26 10:39 上午
 * @description
 */

@Entity
@Table(name = "t_user_material", indexes = {@Index(columnList = "createTime")})
@Data
@EntityListeners(AuditingEntityListener.class)
@DynamicInsert
@DynamicUpdate
public class UserMaterial implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Long userId;

    /**
     * 类型
     * 0：相册
     * 1. 头像（保留）
     * 2：视频（保留）
     * 3：审核视频
     * 11: 相册待审核
     * 12: 相册审核
     */
    @Column(nullable = false, columnDefinition = "int default 0")
    private Integer type = 0;

    /**
     * 当类型为相册相关时 0: 普通照片 1: 守护专属 2: 封面照片
     * 当类型为审核视频时 为4位随机数
     */
    @Column(nullable = false, columnDefinition = "int default 0")
    private Integer purpose = 0;

    /**
     * 目标存储点
     * 0：minio存储
     * 1：腾讯存储
     */
    @Column(nullable = false, columnDefinition = "int default 0")
    private Integer dest = 0;

    // 名称
    @Column
    private String name;

    // 等级 排序用
    @Column
    private Integer level;

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
    private Integer isDelete = 0;
}
