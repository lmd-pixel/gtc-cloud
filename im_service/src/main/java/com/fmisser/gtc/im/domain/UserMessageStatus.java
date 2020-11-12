package com.fmisser.gtc.im.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * 用户之间的单聊消息状态
 * https://cloud.tencent.com/document/product/269/1632
 */

@Data
@Entity
@Table(name = "t_user_message_status")
@EntityListeners(AuditingEntityListener.class)
public class UserMessageStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String userIdFrom;

    @Column
    private String userIdTo;

    @Column
    private int msgSeq;

    @Column
    private int msgRandom;

    @Column
    private int msgTime;

    @Column(nullable = false, unique = true)
    private String msgKey;

    @Column
    private int sendMsgResult;

    @Column
    private String errorInfo;

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

    // 映射多条消息，
    // 设置此为维护端，设置级联删除，急加载，关系链断开删除子端
    @OneToMany(mappedBy = "status", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<UserMessage> userMessages;
}
