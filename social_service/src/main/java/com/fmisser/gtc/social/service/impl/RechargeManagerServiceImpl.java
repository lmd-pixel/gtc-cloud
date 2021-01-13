package com.fmisser.gtc.social.service.impl;

import com.fmisser.gtc.base.dto.social.RechargeDto;
import com.fmisser.gtc.base.dto.social.calc.CalcRechargeDto;
import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.social.repository.RechargeRepository;
import com.fmisser.gtc.social.service.RechargeManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RechargeManagerServiceImpl implements RechargeManagerService {

    private final RechargeRepository rechargeRepository;

    public RechargeManagerServiceImpl(RechargeRepository rechargeRepository) {
        this.rechargeRepository = rechargeRepository;
    }

    @Override
    public Pair<List<RechargeDto>, Map<String, Object>> getRechargeList(String digitId, String nick, Integer status,
                                                       Date startTime, Date endTime,
                                                       Integer pageIndex, Integer pageSize) throws ApiException {
//        Pageable pageable = PageRequest.of(pageIndex, pageSize);

        // status: 0: 未完成 1:已完成 2: 全部
        List<Integer> statusList = new ArrayList<>();
        if (status.equals(0)) {
            statusList.add(1);
            statusList.add(10);
            statusList.add(11);
        } else if (status.equals(1)) {
            statusList.add(20);
            statusList.add(30);
            statusList.add(31);
        } else {
            statusList.add(1);
            statusList.add(10);
            statusList.add(11);
            statusList.add(20);
            statusList.add(30);
            statusList.add(31);
        }

//        Page<RechargeDto> rechargeDtoPage = rechargeRepository
//                .getRechargeList(digitId, nick, startTime, endTime, statusList, pageable);

        List<RechargeDto> rechargeDtoList = rechargeRepository
                .getRechargeList(digitId, nick, startTime, endTime, statusList, pageSize, pageSize * (pageIndex - 1));

        CalcRechargeDto calcRechargeDto = rechargeRepository.calcRecharge(digitId, nick, startTime, endTime, statusList);

        Map<String, Object> extra = new HashMap<>();
        extra.put("totalPage", (calcRechargeDto.getCount() / pageSize) + 1 );
        extra.put("totalEle", calcRechargeDto.getCount());
        extra.put("currPage", pageIndex);
        extra.put("totalCount", calcRechargeDto.getCount());
        extra.put("totolRecharge", calcRechargeDto.getRecharge());

        return Pair.of(rechargeDtoList, extra);
    }
}
