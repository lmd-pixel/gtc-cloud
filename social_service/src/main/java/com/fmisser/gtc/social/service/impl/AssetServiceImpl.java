package com.fmisser.gtc.social.service.impl;

import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.social.domain.Asset;
import com.fmisser.gtc.social.domain.User;
import com.fmisser.gtc.social.repository.AssetRepository;
import com.fmisser.gtc.social.service.AssetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class AssetServiceImpl implements AssetService {

    private final AssetRepository assetRepository;

    public AssetServiceImpl(AssetRepository assetRepository) {
        this.assetRepository = assetRepository;
    }

    @Override
    public Asset getAsset(User user) throws ApiException {
        return assetRepository.findByUserId(user.getId());
    }

    @Override
    public Asset bindAlipay(User user, String alipayName, String alipayNumber) throws ApiException {
        if (StringUtils.isEmpty(alipayName) || StringUtils.isEmpty(alipayNumber)) {
            throw new ApiException(-1, "信息无效!");
        }

        Asset asset = assetRepository.findByUserId(user.getId());
        asset.setAlipayName(alipayName);
        asset.setAlipayNumber(alipayNumber);
        return assetRepository.save(asset);
    }
}
