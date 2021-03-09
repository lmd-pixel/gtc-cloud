package com.fmisser.gtc.base.dto.social;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

/**
 * 封号
 */

public interface ForbiddenDto {

    Long getId();

    String getDigitId();

    String getNick();

    Integer getIdentity();

    Integer getDays();

    String getMessage();

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    Date getStartTime();

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    Date getEndTime();
}
