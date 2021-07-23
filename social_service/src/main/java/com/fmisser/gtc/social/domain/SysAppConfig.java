package com.fmisser.gtc.social.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;

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

//    @Column
//    private DateTime dynamiChangeTime;

    @Column
    private String vedioViewIsFee;

    @Column
    private String vedioActualIsFee;







}
