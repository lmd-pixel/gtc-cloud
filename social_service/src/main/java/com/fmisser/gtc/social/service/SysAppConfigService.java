package com.fmisser.gtc.social.service;

import com.fmisser.gtc.base.exception.ApiException;

import java.text.ParseException;

public interface SysAppConfigService {
    String getAppAuditVersionEx(String channelId,String version) throws ApiException, ParseException;
}
