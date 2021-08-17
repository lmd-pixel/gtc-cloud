package com.fmisser.gtc.social.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

/**
 * @author fmisser
 * @create 2021-04-20 下午2:39
 * @description 用户设备信息
 */
@Entity
@Table(name = "t_user_device", indexes =
        {@Index(columnList = "createTime"), @Index(columnList = "userId")})
@Data
@EntityListeners(AuditingEntityListener.class)
public class UserDevice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Long userId;

    @Column
    private int deviceType;//区分苹果，安卓，华为

    @Column
    private String deviceName;//设备名称


    @Column
    private String deviceCategory; //设备型号


    @Column
    private String deviceIdfa; //苹果广告标识

    @Column
    private String deviceImei;

    @Column
    private String deviceAndroidId;//唯一Id

    @Column
    private String deviceOaid;


    @Column
    private String deviceToken; //用户或者厂商推送的token

    @Column
    private Integer physicalDevice;//是否是虚拟机

    @Column
    private String osVersion;


    @Column
    private String channel; //打包出来的渠道编号

    @Column
    private String deviceDescribe;//设备描述


    @Column
    private String ipAddr;//设备ip

/*新加字段*/
    @Column
    private String appVersion;//使用的app版本

    @Column
    private String sysVersion;//系统版本

    @Column
    private String deviceBrand;//用户设备品牌（vivo，oppo,华为等）

    @Column
    private String deviceIfv;//苹果设备唯一标识(identifilterForVendor)

    @Column
    private String localizedmodel;


    @Column
    private String lang;//语言

    @CreatedDate
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column
    private Date createTime;



    public  UserDevice(){
       super();
    }


    public  UserDevice(Long userId,String deviceAndroidId){
        this.deviceAndroidId=deviceAndroidId;
        this.userId=userId;
    }
}

