package com.fmisser.gtc.social.service;

import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.social.domain.Asset;
import com.fmisser.gtc.social.domain.User;

/**
 * 资产相关的接口
 */

public interface AssetService {
    Asset getAsset(User user) throws ApiException;
}
