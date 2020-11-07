package com.fmisser.gtc.im.domain;

import lombok.Data;
import org.springframework.stereotype.Component;

import javax.persistence.*;

/**
 * im消息的结构
 */

@Data
@Entity
@Table(name = "t_user_message")
public class UserMessage {

    //  懒加载，inner join 方式
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "status_id", referencedColumnName = "id")
    private UserMessageStatus status;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String msgType;

    @Column
    private String msgText;

    @Column
    private String msgDesc;

    @Column
    private String msgData;
}

