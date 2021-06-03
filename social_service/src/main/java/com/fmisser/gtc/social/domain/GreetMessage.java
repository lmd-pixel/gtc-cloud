package com.fmisser.gtc.social.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

/**
 * 打招呼
 */

@Entity
@Table(name = "t_greet_message")
@Data
@EntityListeners(AuditingEntityListener.class)
@DynamicInsert
@DynamicUpdate
public class GreetMessage {
    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String message;

    // 0: 女性用语 1: 男性用语
    @Column(nullable = false, columnDefinition = "int default 0")
    private int type = 0;

    @Column(nullable = false, length = 16)
    @ColumnDefault("'zh'")
    private String lang = "zh";

    @Column(nullable = false, columnDefinition = "int default 0")
    private int disable = 0;
}
