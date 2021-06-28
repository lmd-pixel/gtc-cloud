package com.fmisser.gtc.social.service.impl;

import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.base.utils.DateUtils;
import com.fmisser.gtc.social.domain.SysConfig;
import com.fmisser.gtc.social.repository.SysConfigRepository;
import com.fmisser.gtc.social.service.SysConfigService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
    public boolean isMsgFee() throws ApiException {
        SysConfig sysConfig = sysConfigRepository.findByName("ios_audit");
        if (Objects.isNull(sysConfig)) {
            return false;
        }
        return sysConfig.getValue4().equals("0");
    }

    @Override
    public Date getAppAuditDynamicDateLimit(String version) throws ApiException {
        SysConfig sysConfig = sysConfigRepository.findByName("ios_audit");
        if (Objects.isNull(sysConfig)) {
            return null;
        }

        // 没有审核返回空
        if (!sysConfig.getValue1().equals("1")) {
            return null;
        }

        // 没有审核版本号返回空
        if (StringUtils.isEmpty(sysConfig.getValue2())) {
            return null;
        }

        if (!sysConfig.getValue2().equals(version)) {
            return null;
        }

        if (StringUtils.isEmpty(sysConfig.getValue3())) {
            return null;
        } else {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                return dateFormat.parse(sysConfig.getValue3());
            } catch (ParseException e) {
                return null;
            }
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

    @Override
    public boolean isShowFreeVideoBanner() throws ApiException {
        SysConfig sysConfig = sysConfigRepository.findByName("banner_free_video");
        return _commonCheck(sysConfig);
    }

    @Override
    public boolean isShowFreeMsgBanner() throws ApiException {
        SysConfig sysConfig = sysConfigRepository.findByName("banner_free_msg");
        return _commonCheck(sysConfig);
    }

    @Override
    public boolean isShowRechargeVideoBanner() throws ApiException {
        SysConfig sysConfig = sysConfigRepository.findByName("banner_recharge");
        return _commonCheck(sysConfig);
    }

    @Override
    public boolean isRegSendFreeVideo() throws ApiException {
        SysConfig sysConfig = sysConfigRepository.findByName("reg_free_video");
        return _commonCheck(sysConfig);
    }

    @Override
    public boolean isRegSendFreeMsg() throws ApiException {
        SysConfig sysConfig = sysConfigRepository.findByName("reg_free_msg");
        return _commonCheck(sysConfig);
    }

    @Override
    public boolean isFirstRechargeFreeVideo() throws ApiException {
        SysConfig sysConfig = sysConfigRepository.findByName("first_recharge_free_video");
        return _commonCheck(sysConfig);
    }

    @Override
    public boolean isFirstRechargeFreeMsg() throws ApiException {
        SysConfig sysConfig = sysConfigRepository.findByName("first_recharge_free_msg");
        return _commonCheck(sysConfig);
    }

    @Override
    public boolean isRandRecommend() throws ApiException {
        SysConfig sysConfig = sysConfigRepository.findByName("rand_recommend");
        if (Objects.isNull(sysConfig)) {
            return false;
        }

        return sysConfig.getValue1().equals("1");
    }

    @Override
    public BigDecimal getRechargeInviterRewardRatio() throws ApiException {
        SysConfig sysConfig = sysConfigRepository.findByName("recharge_inviter_reward_ratio");
        return BigDecimal.valueOf(Double.parseDouble(sysConfig.getValue1()));
    }

    @Override
    public Integer getDynamicDailyCountLimit() throws ApiException {
        SysConfig sysConfig = sysConfigRepository.findByName("dynamic_limit");
        return Integer.parseInt(sysConfig.getValue1());
    }

    @Override
    public Pair<Integer, Integer> getRegDailyCountLimit() throws ApiException {
        SysConfig sysConfig = sysConfigRepository.findByName("reg_device_limit");
        int ipLimit = Integer.parseInt(sysConfig.getValue1());
        int deviceLimit = Integer.parseInt(sysConfig.getValue2());

        return Pair.of(ipLimit, deviceLimit);
    }

    @SneakyThrows
    private boolean _commonCheck(SysConfig sysConfig) {
        String value2 = sysConfig.getValue2();
        if (value2.equals("0")) {
            // 不开启时间限制
            return true;
        } else if (value2.equals("-1")) {
            // 认为不符合
            return false;
        }

        String value3 = sysConfig.getValue3();
        String value4 = sysConfig.getValue4();

        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        Date now = new Date();
        Date startTime = dateFormat.parse(value3);
        Date endTime = dateFormat.parse(value4);

        // 是否在开启时间内
        return DateUtils.isTimeBetween(now, startTime, endTime);
    }
}
