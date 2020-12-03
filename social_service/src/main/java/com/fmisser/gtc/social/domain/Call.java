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
import java.util.Date;

@Entity
@Table(name = "t_call",
        indexes = {@Index(columnList = "createdTime")})
@Data
@EntityListeners(AuditingEntityListener.class)
@DynamicInsert
@DynamicUpdate
public class Call {
            @Id
            @GeneratedValue(strategy = GenerationType.IDENTITY)
            private Long id;

    // 房间id
    @Column(nullable = false, unique = true)
    private Long roomId;

    // 通话唯一标识
    @Column(nullable = false, unique = true)
    private String commId;

    // 谁发起
    @Column(nullable = false)
    private Long userIdFrom;

    // 谁接收
    @Column(nullable = false)
    private Long userIdTo;

    /**
     * 0：语音
     * 1: 视频
     */
    @Column
    private int type;

    @CreatedDate
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column
    private Date createdTime;

    @LastModifiedDate
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column
    private Date modifyTime;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column
    private Date startTime;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column
    private Date finishTime;

    @Column(nullable = false, columnDefinition = "int default 0")
    private int duration = 0;

    @Column(nullable = false, columnDefinition = "int default 0")
    private int isFinished;
}
