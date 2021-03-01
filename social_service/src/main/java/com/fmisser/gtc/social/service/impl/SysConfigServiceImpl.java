package com.fmisser.gtc.social.service.impl;

import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.social.domain.SysConfig;
import com.fmisser.gtc.social.repository.SysConfigRepository;
import com.fmisser.gtc.social.service.SysConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Objects;

@Service
public class SysConfigServiceImpl implements SysConfigService {

    @Autowired
    private SysConfigRepository sysConfigRepository;

    @Override
    public boolean isAppAudit() throws ApiException {
        SysConfig sysConfig = sysConfigRepository.findByName("ios_audit");
        if (Objects.isNull(sysConfig)) {
            return false;
        }
        return sysConfig.getValue1().equals("1");
    }

    @Override
    public String getAppAuditVersion() throws ApiException {
        SysConfig sysConfig = sysConfigRepository.findByName("ios_audit");
        if (Objects.isNull(sysConfig)) {
            return "";
        }
        if (StringUtils.isEmpty(sysConfig.getValue2())) {
            return "";
        } else {
            return sysConfig.getValue2();
        }
    }

    @Override
    public boolean isMsgGreetEnable() throws ApiException {
        SysConfig sysConfig = sysConfigRepository.findByName("msg_greet");
        if (Objects.isNull(sysConfig)) {
            return false;
        }
        return sysConfig.getValue1().equals("1");
    }

    @Override
    public Pair<BigDecimal, BigDecimal> getWithdrawConfig() throws ApiException {
        SysConfig sysConfig = sysConfigRepository.findByName("withdraw_audit");
        BigDecimal commFee = BigDecimal.valueOf(Double.parseDouble(sysConfig.getValue1()));
        BigDecimal commMinWithdraw = BigDecimal.valueOf(Double.parseDouble(sysConfig.getValue2()));
        return Pair.of(commFee, commMinWithdraw);
    }
}
