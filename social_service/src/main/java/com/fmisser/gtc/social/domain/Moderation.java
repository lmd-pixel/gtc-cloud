package com.fmisser.gtc.social.domain;


import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Entity
@Table(name = "t_moderation")
@Data
@EntityListeners(AuditingEntityListener.class)
@DynamicInsert
@DynamicUpdate
public class Moderation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 24)
    private String label;

    @Column
    private Integer score;

    @Column(length = 24)
    private String suggestion;

    @Column(nullable = false, columnDefinition = "int default 0")
    private int disable = 0;
}
