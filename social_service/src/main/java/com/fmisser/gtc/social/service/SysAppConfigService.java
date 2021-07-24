package com.fmisser.gtc.social.service;

import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.social.domain.SysAppConfig;

import java.util.Date;
import java.text.ParseException;

public interface SysAppConfigService {
    String getAppAuditVersionEx(String channelId,String version) throws ApiException, ParseException;
    Date getAppAuditDynamicDateLimit(String channelId, String version) throws ApiException, ParseException;
    // 获取 app 审核的版本号
    String getAppAuditVersion(String version) throws ApiException;
    boolean getAppAuditVersionTime(String version) throws ApiException;
}
