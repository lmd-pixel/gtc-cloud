package com.fmisser.gtc.social.service.impl;

import com.fmisser.gtc.base.dto.social.PayAuditDto;
import com.fmisser.gtc.base.dto.social.WithdrawAuditDto;
import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.social.domain.Asset;
import com.fmisser.gtc.social.domain.WithdrawAudit;
import com.fmisser.gtc.social.repository.AssetRepository;
import com.fmisser.gtc.social.repository.WithdrawAuditRepository;
import com.fmisser.gtc.social.service.AssetService;
import com.fmisser.gtc.social.service.WithdrawManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Pair;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
public class WithdrawManagerServiceImpl implements WithdrawManagerService {

    private final WithdrawAuditRepository withdrawAuditRepository;
    private final AssetRepository assetRepository;

    public WithdrawManagerServiceImpl(WithdrawAuditRepository withdrawAuditRepository,
                                      AssetRepository assetRepository) {
        this.withdrawAuditRepository = withdrawAuditRepository;
        this.assetRepository = assetRepository;
    }

    @Override
    public Pair<List<WithdrawAuditDto>, Map<String, Object>> getWithdrawAuditList(String digitId, String nick,
                                                             Date startTime, Date endTime,
                                                             int pageIndex, int pageSize) throws ApiException {
        Pageable pageable = PageRequest.of(pageIndex - 1, pageSize);
        Page<WithdrawAuditDto> withdrawAuditDtoPage =  withdrawAuditRepository
                .getWithdrawAuditList(digitId, nick, startTime, endTime, pageable);

        int totalPage = withdrawAuditDtoPage.getTotalPages();
        long totalEle = withdrawAuditDtoPage.getTotalElements();


        WithdrawAuditDto  withdrawAuditDto= withdrawAuditRepository.withdrawAudit(digitId, nick,  startTime,endTime);


        Map<String, Object> extra = new HashMap<>();
        extra.put("totalPage", totalPage);
        extra.put("totalEle", totalEle);
        extra.put("currPage", pageIndex);

        extra.put("totalCount", withdrawAuditDto.getCount());
        extra.put("totolRecharge", withdrawAuditDto.getRecharge());


        return Pair.of(withdrawAuditDtoPage.getContent(), extra);
    }

    @Override
    public Pair<List<PayAuditDto>, Map<String, Object>> getPayAuditList(String digitId, String nick, Integer type,
                                             Date startTime, Date endTime,
                                             int pageIndex, int pageSize) throws ApiException {
        Pageable pageable = PageRequest.of(pageIndex - 1, pageSize);
        Page<PayAuditDto> payAuditDtoPage = withdrawAuditRepository
                .getPayAuditList(digitId, nick, type, startTime, endTime, pageable);

        Map<String, Object> extra = new HashMap<>();
        extra.put("totalPage", payAuditDtoPage.getTotalPages());
        extra.put("totalEle", payAuditDtoPage.getTotalElements());
        extra.put("currPage", pageIndex);

        return Pair.of(payAuditDtoPage.getContent(), extra);
    }

    @Transactional
//    @Retryable
    @Override
    public int withdrawAudit(String orderNumber, int operate, String message) throws ApiException {
        Optional<WithdrawAudit> withdrawAuditOptional = withdrawAuditRepository.findByOrderNumber(orderNumber);
        if (!withdrawAuditOptional.isPresent()) {
            throw new ApiException(-1, "提现订单不存在");
        }

        WithdrawAudit withdrawAudit = withdrawAuditOptional.get();
        if (withdrawAudit.getStatus() != 10) {
            throw new ApiException(-1, "该提现订单目前无法审核");
        }

        if (operate == 1) {
            // 审核通过，待打款
            withdrawAudit.setStatus(30);
        } else {
            // 审核未通过
            withdrawAudit.setStatus(20);

            // 返还聊币
            assetRepository.addCoin(withdrawAudit.getUserId(), withdrawAudit.getDrawCurr());
        }

        if (Objects.nonNull(message)) {
            withdrawAudit.setRemark(message);
        }

        withdrawAuditRepository.save(withdrawAudit);
        return 1;
    }

    @Transactional
//    @Retryable
    @Override
    public int payAudit(String orderNumber, int operate, Double payActual, String message) throws ApiException {
        Optional<WithdrawAudit> withdrawAuditOptional = withdrawAuditRepository.findByOrderNumber(orderNumber);
        if (!withdrawAuditOptional.isPresent()) {
            throw new ApiException(-1, "提现订单不存在");
        }

        WithdrawAudit withdrawAudit = withdrawAuditOptional.get();
        if (withdrawAudit.getStatus() != 30) {
            throw new ApiException(-1, "该提现订单目前无法打款");
        }

        if (operate == 2) {
            // 部分打款 只针对特殊情况，部分打款不会返还部分聊币
            withdrawAudit.setStatus(42);
            withdrawAudit.setPayActual(BigDecimal.valueOf(payActual));
        } else if (operate == 1) {
            // 打款完成
            withdrawAudit.setStatus(40);
            withdrawAudit.setPayActual(withdrawAudit.getPayMoney());
        } else {
            // 打款失败
            withdrawAudit.setStatus(41);

            // 返还聊币
            assetRepository.addCoin(withdrawAudit.getUserId(), withdrawAudit.getDrawCurr());
        }

        if (Objects.nonNull(message)) {
            withdrawAudit.setRemark(message);
        }

        withdrawAuditRepository.save(withdrawAudit);
        return 1;
    }

    @Override
    public Pair<List<WithdrawAuditDto>, Map<String, Object>> getWithdrawList(String digitId, String nick,
                                                  Date startTime, Date endTime,
                                                  Integer status,
                                                  int pageIndex, int pageSize) throws ApiException {
        Pageable pageable = PageRequest.of(pageIndex - 1, pageSize);

        // status: 0:待审核 1:待打款 2: 审核未通过 3：打款完成 4：全部
        List<Integer> statusList = new ArrayList<>();
        if (status.equals(0)) {
            statusList.add(10);
        } else if (status.equals(1)) {
            statusList.add(30);
        } else if (status.equals(2)) {
            statusList.add(20);
        } else if (status.equals(3)) {
            statusList.add(40);
        } else {
            statusList.add(10);
            statusList.add(20);
            statusList.add(30);
            statusList.add(40);
        }

        Page<WithdrawAuditDto> withdrawAuditDtoPage = withdrawAuditRepository
                .getWithdrawList(digitId, nick, startTime, endTime, statusList, pageable);

        WithdrawAuditDto  withdrawAuditDto= withdrawAuditRepository.withdrawAuditList(digitId, nick,  startTime,endTime,statusList);

        Map<String, Object> extra = new HashMap<>();
        extra.put("totalPage", withdrawAuditDtoPage.getTotalPages());
        extra.put("totalEle", withdrawAuditDtoPage.getTotalElements());
        extra.put("currPage", pageIndex);

        extra.put("totalCount", withdrawAuditDto.getCount());
        extra.put("totolRecharge", withdrawAuditDto.getRecharge());


        return Pair.of(withdrawAuditDtoPage.getContent(), extra);
    }
}
