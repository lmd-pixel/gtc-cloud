package com.fmisser.gtc.social.service;

import com.fmisser.gtc.base.dto.social.PayAuditDto;
import com.fmisser.gtc.base.dto.social.WithdrawAuditDto;
import com.fmisser.gtc.base.exception.ApiException;
import org.springframework.data.util.Pair;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface WithdrawManagerService {
    // 获取审核列表
    Pair<List<WithdrawAuditDto>, Map<String, Object>> getWithdrawAuditList(String digitId, String nick,
                                                                           Date startTime, Date endTime,
                                                                           int pageIndex, int pageSize) throws ApiException;

    // 获取打款列表
    Pair<List<PayAuditDto>, Map<String, Object>> getPayAuditList(String digitId, String nick, Integer type,
                                      Date startTime, Date endTime,
                                      int pageIndex, int pageSize) throws ApiException;

    // 审核
    int withdrawAudit(String orderNumber, int operate, String message) throws ApiException;

    // 打款
    int payAudit(String orderNumber, int operate, Double payActual, String message) throws ApiException;

    // 获取提现列表
    Pair<List<WithdrawAuditDto>, Map<String, Object>> getWithdrawList(String digitId, String nick,
                                           Date startTime, Date endTime,
                                           Integer status,
                                           int pageIndex, int pageSize) throws ApiException;
}


