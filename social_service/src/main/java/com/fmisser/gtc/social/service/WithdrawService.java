package com.fmisser.gtc.social.service;

import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.social.domain.User;
import com.fmisser.gtc.social.domain.WithdrawAudit;

import java.math.BigDecimal;

public interface WithdrawService {
    WithdrawAudit requestWithdraw(User user, BigDecimal coin) throws ApiException;

    /**
     * 当前是否存在审核中的提现
     * @param user
     * @return
     * @throws ApiException
     */
    WithdrawAudit getCurrWithdraw(User user) throws ApiException;
}
