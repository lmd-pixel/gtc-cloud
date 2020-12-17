package com.fmisser.gtc.social.service;

import com.fmisser.gtc.base.dto.social.IapReceiptDto;
import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.social.domain.IapReceipt;

/**
 * 事务服务，主要为了避免同一个事务在一个class中调用失效的问题
 */
public interface TransactionService {
    // iap 支付成功
    String iapPaySuccess(String transactionId) throws ApiException;
}
