package com.fmisser.gtc.social.service;

import com.fmisser.gtc.base.dto.social.RechargeDto;
import com.fmisser.gtc.base.exception.ApiException;

import java.util.Date;
import java.util.List;

public interface RechargeManagerService {
    List<RechargeDto> getRechargeList(String digitId, String nick, Integer status,
                                      Date startTime, Date endTime,
                                      Integer pageIndex, Integer pageSize) throws ApiException;
}
