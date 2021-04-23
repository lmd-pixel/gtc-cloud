package com.fmisser.gtc.social.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 活跃行为，记录玩家的行为
 */

@Entity
@Table(name = "t_active",
        indexes = {@Index(columnList = "activeTime"),@Index(columnList = "userId")})
@Data
@EntityListeners({AuditingEntityListener.class})
@DynamicInsert
@DynamicUpdate
public class Active {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    // TODO: 2021/1/25 身份会随着时间改变，根据此字段判断的逻辑可能存在问题，建议动态获取
    @Column(nullable = false)
    private int identity;

    /**
     * 最近活跃状态
     * 1：打开app，登录账号 记录地理位置
     * 2：退出账号
     * 10: 语音聊天 关联room id, 相关人
     * 11：视频聊天 关联room id，相关人
     * 12：语音聊天结束 关联room id，相关人
     * 13: 视频聊天结束 关联room id, 相关人
     * 20: 发布动态 关联动态id
     * 21: 评论动态 关联动态id
     * 31: 关注信息 关联相关人
     * 41: 登陆im 记录ip 平台
     * 42：连续登录im 记录ip 平台
     * 43: 登出im 记录上一次在线时长
     * 44：连续的登出im，时长记做0
     * 101：发起通话邀请
     * 102: 取消通话邀请
     * 103: 通话邀请超时
     * 104: 拒绝通话邀请
     * 105: 接受通话邀请
     * 106: 结束通话邀请
     * 107: 确认通话
     * 201: 通话更新
     */
    @Column(nullable = false)
    private int status;

    /**
     * type = 0 用户活跃状态
     * typ= 1 用户通话状态
     * type = 2
     */
    @Column(columnDefinition = "int default 0")
    private int type = 0;

    @Column
    private BigDecimal longitude;

    @Column
    private BigDecimal latitude;

    @Column
    private String ip;

    @Column
    private String platform;

    @Column
    private Long duration;

    @Column
    private Long roomId;

    @Column
    private Long dynamicId;

    @Column
    private Long relatedUserId;

    @CreatedDate
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column
    private Date activeTime;
}
