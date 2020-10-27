package com.fmisser.gtc.social.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 活跃行为，记录玩家的最新的行为
 * 注意只记录自己主动发起的行为，被动接收的不记录
 * 聊天是双向的，所有都记录
 */

@Entity
@Table(name = "t_active",
        indexes = @Index(columnList = "activeTime"))
@Data
@EntityListeners({AuditingEntityListener.class})
@DynamicInsert
@DynamicUpdate
public class Active {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long youngId;

    /**
     * 最近活跃状态
     * 1：上线  记录地理位置
     * 10: 语音聊天 关联相关人
     * 11：视频聊天 关联相关人
     * 20: 发布动态 关联动态id
     * 21: 评论动态 关联动态id
     * 31: 关注信息 关联相关人
     * 41:
     */
    @Column(nullable = false)
    private int activeStatus;

    @LastModifiedDate
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column
    private Date activeTime;

    @Column
    private BigDecimal longitude;

    @Column
    private BigDecimal latitude;

    @Column
    private Long relatedDynamic;

    @Column
    private Long relatedYong;
}
