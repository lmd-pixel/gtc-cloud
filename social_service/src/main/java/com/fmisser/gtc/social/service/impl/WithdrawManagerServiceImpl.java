package com.fmisser.gtc.social.service.impl;

import com.fmisser.gtc.base.dto.social.PayAuditDto;
import com.fmisser.gtc.base.dto.social.WithdrawAuditDto;
import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.social.domain.WithdrawAudit;
import com.fmisser.gtc.social.repository.WithdrawAuditRepository;
import com.fmisser.gtc.social.service.WithdrawManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
public class WithdrawManagerServiceImpl implements WithdrawManagerService {

    private final WithdrawAuditRepository withdrawAuditRepository;

    public WithdrawManagerServiceImpl(WithdrawAuditRepository withdrawAuditRepository) {
        this.withdrawAuditRepository = withdrawAuditRepository;
    }

    @Override
    public List<WithdrawAuditDto> getWithdrawAuditList(String digitId, String nick,
                                                       Date startTime, Date endTime,
                                                       int pageIndex, int pageSize) throws ApiException {
        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        return withdrawAuditRepository
                .getWithdrawAuditList(digitId, nick, startTime, endTime, pageable)
                .getContent();
    }

    @Override
    public List<PayAuditDto> getPayAuditList(String digitId, String nick, Integer type,
                                             Date startTime, Date endTime,
                                             int pageIndex, int pageSize) throws ApiException {
        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        return withdrawAuditRepository
                .getPayAuditList(digitId, nick, type, startTime, endTime, pageable)
                .getContent();
    }

    @Transactional
    @Retryable
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
        }

        if (Objects.nonNull(message)) {
            withdrawAudit.setRemark(message);
        }

        withdrawAuditRepository.save(withdrawAudit);
        return 1;
    }

    @Transactional
    @Retryable
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
            // 部分打款
            withdrawAudit.setStatus(42);
            withdrawAudit.setPayActual(BigDecimal.valueOf(payActual));
        } else if (operate == 1) {
            // 打款完成
            withdrawAudit.setStatus(40);
            withdrawAudit.setPayActual(withdrawAudit.getPayMoney());
        } else {
            // 打款失败
            withdrawAudit.setStatus(41);
        }

        if (Objects.nonNull(message)) {
            withdrawAudit.setRemark(message);
        }

        withdrawAuditRepository.save(withdrawAudit);
        return 1;
    }

    @Override
    public List<WithdrawAuditDto> getWithdrawList(String digitId, String nick,
                                                  Date startTime, Date endTime,
                                                  Integer status,
                                                  int pageIndex, int pageSize) throws ApiException {
        Pageable pageable = PageRequest.of(pageIndex, pageSize);

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

        return withdrawAuditRepository
                .getWithdrawList(digitId, nick, startTime, endTime, statusList, pageable)
                .getContent();
    }
}
