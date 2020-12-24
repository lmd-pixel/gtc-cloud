package com.fmisser.gtc.social.service;

import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.social.domain.User;

public interface ReportService {
    // 举报某个人
    int reportUser(User user, User dstUser, String message) throws ApiException;
    // 举报某条动态
    int reportDynamic(User user, Long dynamicId, String message) throws ApiException;
}
