package com.fmisser.gtc.social.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "t_user_message",
        indexes = {@Index(columnList = "createTime"),
                @Index(columnList = "digitIdFrom"),
                @Index(columnList = "digitIdTo")})
@Data
@EntityListeners(AuditingEntityListener.class)
public class UserMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 发送人的数字ID
    @Column(length = 16)
    private String digitIdFrom;

    // 接收人的数字ID
    @Column(length = 16)
    private String digitIdTo;

    // 消息序列值
    @Column
    private Long msgSeq;

    // 消息随机值
    @Column
    private Long msgRandom;

    // 消息时间
    @Column
    private Long msgTime;

    // 消息key
    @Column
    private String msgKey;

    // 消息类型 TIMTextElem：文本
    @Column
    private String msgType;

    // 消息文本，当消息类型为TIMTextElem时，这个字段是文本内容
    @Column(length = 1024)
    private String msgText;

    // 消息描述，当消息类型不是TIMTextElem时使用
    private String msgDesc;

    // 消息数据, 当消息类型不是TIMTextElem时使用
    @Column(length = 1024)
    private String msgData;

    // 创建时间
    @CreatedDate
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column
    private Date createTime;

    @Column
    private int pass;
}
