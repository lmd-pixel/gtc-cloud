package com.fmisser.gtc.social.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 动态，个人更新的动态数据
 */
@Entity
@Table(name = "t_dynamic",
        indexes = {@Index(columnList = "userId,isDelete,createTime")})
@Data
@EntityListeners(AuditingEntityListener.class)
public class Dynamic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column
    private String content;

    /**
     * 发布的媒体类型 0: 纯文本， 1： 图片 2：视频
     */
    @Column(nullable = false)
    private int type = 0;

    @Column
    private String video;

    @Column
    private String pictures;

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

    @Column
    private BigDecimal longitude;

    @Column
    private BigDecimal latitude;

    @Column(nullable = false, columnDefinition = "int default 0")
    private int heart = 0;

    @Column(nullable = false, columnDefinition = "int default 0")
    private int comment = 0;

    @Column(nullable = false, columnDefinition = "int default 0")
    private int isDelete = 0;

    @OneToMany(mappedBy = "dynamic", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Interact> interacts = new ArrayList<>();

    @JsonIgnore
    @Version
    private Long version;
}
