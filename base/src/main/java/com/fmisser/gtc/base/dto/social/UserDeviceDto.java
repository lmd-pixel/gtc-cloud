package com.fmisser.gtc.base.dto.social;

import java.math.BigDecimal;
import java.util.Date;

public class UserDeviceDto {
    private long id;
    private  Long userId;
    private String ipAddr;
    private String deviceAndroidId;


    public UserDeviceDto(Long id, Long userId, String ipAddr, String deviceAndroidId) {
        this.id=id;
        this.userId=userId;
        this.ipAddr=ipAddr;
        this.deviceAndroidId=deviceAndroidId;
    }
}
