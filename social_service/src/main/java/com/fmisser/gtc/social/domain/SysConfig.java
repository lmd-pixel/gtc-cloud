package com.fmisser.gtc.social.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Entity
@Table(name = "t_sys_config")
@Data
public class SysConfig {
    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column
    private String remark;

    @Column
    private String key1;

    @Column
    private String key2;

    @Column
    private String key3;

    @Column
    private String key4;

    @Column
    private String key5;

    @Column
    private String key6;

    @Column
    private String key7;

    @Column
    private String key8;

    @Column
    private String key9;

    @Column
    private String value1;

    @Column
    private String value2;

    @Column
    private String value3;

    @Column
    private String value4;

    @Column
    private String value5;

    @Column
    private String value6;

    @Column
    private String value7;

    @Column
    private String value8;

    @Column
    private String value9;
}
