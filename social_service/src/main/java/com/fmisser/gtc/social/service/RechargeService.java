package com.fmisser.gtc.social.service;

import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.social.domain.User;

import java.util.Date;

public interface RechargeService {
    String IapVerifyReceipt(User user,
                            String receipt, int env,
                            String productId, String transactionId,
                            Date purchaseDate) throws ApiException;

    Long getUserRechargeCount(User user) throws ApiException;
}
