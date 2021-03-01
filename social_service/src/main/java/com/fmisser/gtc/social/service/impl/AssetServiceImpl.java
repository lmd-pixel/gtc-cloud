package com.fmisser.gtc.social.service.impl;

import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.social.domain.Asset;
import com.fmisser.gtc.social.domain.SysConfig;
import com.fmisser.gtc.social.domain.User;
import com.fmisser.gtc.social.repository.AssetRepository;
import com.fmisser.gtc.social.service.AssetService;
import com.fmisser.gtc.social.service.SysConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;

@Service
public class AssetServiceImpl implements AssetService {

    private final AssetRepository assetRepository;
    private final SysConfigService sysConfigService;

    public AssetServiceImpl(AssetRepository assetRepository,
                            SysConfigService sysConfigService) {
        this.assetRepository = assetRepository;
        this.sysConfigService = sysConfigService;
    }

    @Override
    public Asset getAsset(User user) throws ApiException {
        Asset asset = assetRepository.findByUserId(user.getId());

        // 提现手续费和最低提现金额
        Pair<BigDecimal, BigDecimal> withdrawConfig = sysConfigService.getWithdrawConfig();
        asset.setCommWithdrawFee(withdrawConfig.getFirst());
        asset.setCommMinWithdraw(withdrawConfig.getSecond());

        return asset;
    }

    @Override
    public Asset bindAlipay(User user, String alipayName, String alipayNumber, String phone) throws ApiException {
        if (StringUtils.isEmpty(alipayName) || StringUtils.isEmpty(alipayNumber)) {
            throw new ApiException(-1, "信息无效!");
        }

        Asset asset = assetRepository.findByUserId(user.getId());
        asset.setAlipayName(alipayName);
        asset.setAlipayNumber(alipayNumber);
        asset.setPhone(phone);
        return assetRepository.save(asset);
    }
}
