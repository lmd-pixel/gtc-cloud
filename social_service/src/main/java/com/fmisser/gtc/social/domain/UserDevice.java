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
    private int deviceType;

    @Column
    private String deviceName;

    @Column
    private String deviceCategory;

    @Column
    private String deviceIdfa;

    @Column
    private String deviceToken;

    @Column
    private String ipAddr;

    @Column
    private String lang;

    @CreatedDate
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column
    private Date createTime;
}

