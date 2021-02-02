package com.fmisser.gtc.social.service;

import com.fmisser.gtc.base.exception.ApiException;

public interface SysConfigService {

    // 是否ios app在审核
    boolean isAppAudit() throws ApiException;

    // 获取 app 审核的版本号
    String getAppAuditVersion() throws ApiException;

    // 消息骚扰是否打开
    boolean isMsgGreetEnable() throws ApiException;
}
