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
import java.util.*;

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

    @JsonIgnore
    @Column
    private String video;

    @JsonIgnore
    @Column(length = 4096)
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

    @Column
    private String city;

    @Column(nullable = false, columnDefinition = "int default 0")
    private int isDelete = 0;

    @Column
    private String message;

    // 状态：
    // 1： 审核中
    // 10： 审核通过
    // 20：审核不通过
    @Column(nullable = false, columnDefinition = "int default 10")
    private int status = 1;

//    @OneToMany(mappedBy = "dynamic", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
//    private List<DynamicComment> comments = new ArrayList<>();

//    @OneToMany(mappedBy = "dynamic", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
//    private Set<DynamicHeart> hearts = new LinkedHashSet<>();

//    @JsonIgnore
//    @Version
//    private Long version;

}
