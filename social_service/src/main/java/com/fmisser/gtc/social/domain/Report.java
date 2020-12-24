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
import java.util.Date;

/**
 * 举报信息
 */

@Entity
@Table(name = "t_report", indexes = {@Index(columnList = "createTime")})
@Data
@EntityListeners(AuditingEntityListener.class)
@DynamicInsert
@DynamicUpdate
public class Report {

    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Long userId;

    // 举报的类型 1： 用户， 2： 用户动态
    @Column
    private int type;

    // 举报的对象的id
    @Column
    private Long dstUserId;

    // 相关id， 如果是动态，则是动态id
    @Column
    private Long dstRelatedId;

    // 举报内容
    @Column
    private String message;

    // 备注
    @Column
    private String remark;

    // 是否有效, 用户举报默认为0,
    @Column(nullable = false, columnDefinition = "int default 0")
    private int isValid = 0;

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
