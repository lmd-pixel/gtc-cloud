package com.fmisser.gtc.social.domain;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Table(name = "t_sys_app_config")
@Data
public class SysAppConfig {

    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    @Column(unique = true, nullable = false)
    private String name;




    private String version;

    private String channelId;


    private String week;

    private String startTime;

    private String endTime;

   @Column
    private String versionStatus;

    @Column
    private String dynamicIsSwitch;

    @LastModifiedDate
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column
    private Date dynamiChangeTime;

    @Column
    private String vedioViewIsFee;//根据审核版本判断通话是否展示收费

    @Column
    private String vedioActualIsFee;//根据过审版本判断通话是否需要收费

    @Column
    private String harassIsStart;//根据版本判断是否开启骚扰模式







}
