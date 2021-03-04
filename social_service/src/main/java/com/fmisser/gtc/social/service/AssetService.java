package com.fmisser.gtc.social.service;

import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.social.domain.Asset;
import com.fmisser.gtc.social.domain.User;

import java.math.BigDecimal;

/**
 * 资产相关的接口
 */

public interface AssetService {
    Asset getAsset(User user) throws ApiException;

    Asset bindAlipay(User user, String alipayName, String alipayNumber, String phone) throws ApiException;

    Asset updateProfitRatio(User user,
                            BigDecimal videoProfitRatio,
                            BigDecimal voiceProfitRatio,
                            BigDecimal giftProfitRatio,
                            BigDecimal msgProfitRatio) throws ApiException;
}
