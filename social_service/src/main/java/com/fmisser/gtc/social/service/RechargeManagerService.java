package com.fmisser.gtc.social.service;

import com.fmisser.gtc.base.dto.social.RechargeDto;
import com.fmisser.gtc.base.exception.ApiException;
import org.springframework.data.util.Pair;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface RechargeManagerService {
    Pair<List<RechargeDto>, Map<String, Object>> getRechargeList(String digitId, String nick, Integer status,
                                                                 Date startTime, Date endTime,
                                                                 Integer pageIndex, Integer pageSize,String channelId) throws ApiException;
}
