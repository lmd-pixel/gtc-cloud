package com.fmisser.gtc.social.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * 用户的个人资产信息
 */

@Entity
@Table(name = "t_asset")
@Data
public class Asset {

//    @OneToOne(fetch = FetchType.LAZY, optional = false)
//    @JoinColumn(name = "user_id", referencedColumnName = "id")
//    private User user;

    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @Column(nullable = false, unique = true)
    private Long userId;

    @Column(nullable = false)
    private BigDecimal coin = BigDecimal.ZERO;

    @Column(nullable = false, columnDefinition = "int default 0")
    private int freeDialDuration = 0;

    @Column(nullable = false, columnDefinition = "int default 0")
    private int freeAnswerDuration = 0;

    @Column(name = "vip_level", nullable = false, columnDefinition = "int default 0")
    private int vipLevel = 0;

    @JsonIgnore
    @Version
    private Long version;
}
