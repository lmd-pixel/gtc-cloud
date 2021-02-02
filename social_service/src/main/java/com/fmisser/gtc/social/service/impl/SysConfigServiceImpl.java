package com.fmisser.gtc.social.service.impl;

import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.social.domain.SysConfig;
import com.fmisser.gtc.social.repository.SysConfigRepository;
import com.fmisser.gtc.social.service.SysConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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
}
