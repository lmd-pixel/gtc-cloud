package com.fmisser.gtc.base.dto.social;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

/***
 * 封设备或者ip
 */
public interface DeviceForbiddenDto {
    Long getId();

    String getDigitId();

    Integer getIdentity();

    String getNick();

    Integer getDays();

    String getMessage();

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    Date getStartTime();

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    Date getEndTime();

    String getIpAdress();

    String getDeviceName();
}
