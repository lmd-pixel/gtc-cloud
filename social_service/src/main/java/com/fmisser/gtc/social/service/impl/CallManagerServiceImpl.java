package com.fmisser.gtc.social.service.impl;

import com.fmisser.gtc.base.dto.social.CallDetailDto;
import com.fmisser.gtc.base.dto.social.CallDto;
import com.fmisser.gtc.base.dto.social.RechargeDto;
import com.fmisser.gtc.base.dto.social.calc.CalcTotalCallDto;
import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.social.repository.CallRepository;
import com.fmisser.gtc.social.service.CallManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CallManagerServiceImpl implements CallManagerService {

    private final CallRepository callRepository;

    public CallManagerServiceImpl(CallRepository callRepository) {
        this.callRepository = callRepository;
    }

    @Override
    public Pair<List<CallDto>, Map<String, Object>> getCallList(String callDigitId, String callNick, String acceptDigitId, String acceptNick,
                                                             Integer type, Integer connected, Date startTime, Date endTime,
                                                             Integer pageIndex, Integer pageSize) throws ApiException {
        List<CallDto> callDtoList = callRepository.getCallList(callDigitId, callNick, acceptDigitId, acceptNick,
                type, connected, startTime, endTime, pageSize, (pageIndex - 1) * pageSize);

        CalcTotalCallDto calcTotalCallDto = callRepository
                .calcTotalCall(callDigitId, callNick, acceptDigitId, acceptNick, type, connected, startTime, endTime);
        CalcTotalCallDto calcTotalCallDtoForVideCard = callRepository
                .calcTotalCallFreeCard(callDigitId, callNick, acceptDigitId, acceptNick, type, connected, startTime, endTime);


        Map<String, Object> extra = new HashMap<>();
        extra.put("totalPage", (calcTotalCallDto.getCallTimes() / pageSize) + 1 );
        extra.put("totalEle", calcTotalCallDto.getCallTimes());
        extra.put("currPage", pageIndex);
        extra.put("callUsers", calcTotalCallDto.getCallUsers());
        extra.put("callTimes", calcTotalCallDto.getCallTimes());
        extra.put("acceptUsers", calcTotalCallDto.getAcceptUsers());
        extra.put("acceptTimes", calcTotalCallDto.getAcceptTimes());
        extra.put("totalDuration", calcTotalCallDto.getDuration());
        if (calcTotalCallDto.getAcceptTimes() == 0) {
            extra.put("avgDuration", 0);
            extra.put("connectedRatio", 0);
        } else {
            Long avgDuration = (Long) calcTotalCallDto.getDuration() / calcTotalCallDto.getAcceptTimes();
            extra.put("avgDuration", avgDuration);

            BigDecimal connectedRatio = BigDecimal.valueOf(calcTotalCallDto.getAcceptTimes())
                    .divide(BigDecimal.valueOf(calcTotalCallDto.getCallTimes()), 2, BigDecimal.ROUND_HALF_UP);
            extra.put("connectedRatio", connectedRatio);
        }
        extra.put("videoCard", calcTotalCallDtoForVideCard.getVideoCard());

        return Pair.of(callDtoList, extra);
    }

    @Override
    public CallDetailDto getCallDetail(Long callId) throws ApiException {
        return callRepository.getCallDetail(callId);
    }
}
