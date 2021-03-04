package com.fmisser.gtc.social.service;

import com.fmisser.gtc.base.dto.social.CallDetailDto;
import com.fmisser.gtc.base.dto.social.CallDto;
import com.fmisser.gtc.base.dto.social.calc.CalcTotalCallDto;
import com.fmisser.gtc.base.exception.ApiException;
import org.springframework.data.util.Pair;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface CallManagerService {
    // 获取通话列表
    Pair<List<CallDto>, Map<String, Object>> getCallList(String callDigitId, String callNick,
                                                          String acceptDigitId, String acceptNick,
                                                          Integer type, Integer connected,
                                                          Date startTime, Date endTime,
                                                          Integer pageIndex, Integer pageSize) throws ApiException;

    // 获取通话详情
    CallDetailDto getCallDetail(Long callId) throws ApiException;
}
