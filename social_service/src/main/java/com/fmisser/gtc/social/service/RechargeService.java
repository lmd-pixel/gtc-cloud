package com.fmisser.gtc.social.service;

import com.fmisser.gtc.base.exception.ApiException;

public interface RechargeService {
    String IapVerifyReceipt(String userId, String receipt, boolean chooseEnv) throws ApiException;
}
