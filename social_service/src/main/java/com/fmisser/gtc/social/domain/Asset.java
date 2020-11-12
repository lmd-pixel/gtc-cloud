package com.fmisser.gtc.social.domain;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * 用户的个人资产信息
 */

@Entity
@Table(name = "t_asset", indexes = {@Index(columnList = "coin")})
@Data
public class Asset {

    //  急加载，inner join 方式
    @OneToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private BigDecimal coin = BigDecimal.ZERO;

    @Column(nullable = false, columnDefinition = "int default 0")
    private int freeDialDuration = 0;

    @Column(nullable = false, columnDefinition = "int default 0")
    private int freeAnswerDuration = 0;


}
