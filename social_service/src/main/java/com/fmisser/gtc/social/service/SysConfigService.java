package com.fmisser.gtc.social.service;

import com.fmisser.gtc.base.exception.ApiException;
import org.springframework.data.util.Pair;

import java.math.BigDecimal;

public interface SysConfigService {

    // 是否ios app在审核
    boolean isAppAudit() throws ApiException;

    // 获取 app 审核的版本号
    String getAppAuditVersion() throws ApiException;

    // 消息骚扰是否打开
    boolean isMsgGreetEnable() throws ApiException;

    // 获取提现配置
    // 返回提现手续费和最低提现金额
    Pair<BigDecimal, BigDecimal> getWithdrawConfig() throws ApiException;

    boolean isShowFreeVideoBanner() throws ApiException;

    boolean isShowFreeMsgBanner() throws ApiException;

    boolean isShowRechargeVideoBanner() throws ApiException;

    boolean isRegSendFreeVideo() throws ApiException;

    boolean isRegSendFreeMsg() throws ApiException;

    boolean isFirstRechargeFreeVideo() throws ApiException;

    boolean isFirstRechargeFreeMsg() throws ApiException;
}
