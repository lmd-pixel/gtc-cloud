package com.fmisser.gtc.social.domain;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "t_district")
@Data
public class District {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    // parent id
    @Column
    private int pid;

    // 城市的名字
    @Column
    private String districtName;

    // 城市的类型 0是国，1是省，2是市，3是区
    @Column
    private int type;

    // 地区所处的层级
    @Column
    private int hierarchy;

    // 层级序列
    @Column
    private String districtSqe;

    @Transient
    private List<District> subs;
}
