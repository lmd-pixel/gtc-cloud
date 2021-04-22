package com.fmisser.gtc.social.service;

import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.social.domain.User;

import java.util.Date;

public interface RechargeService {

    String createOrder(User user, Long productId) throws ApiException;

    String updateOrder(User user, String orderNumber, int status) throws ApiException;

    String completeIapOrder(User user, String orderNumber, int env, String receipt,
                            String iapProductId, String transactionId, Date purchaseDate) throws ApiException;

    String IapVerifyReceipt(User user,
                            String receipt, int env,
                            String productId, String transactionId,
                            Date purchaseDate) throws ApiException;

    Long getUserRechargeCount(User user) throws ApiException;
}
