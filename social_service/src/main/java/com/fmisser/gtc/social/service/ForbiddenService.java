package com.fmisser.gtc.social.service;

import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.social.domain.Forbidden;
import com.fmisser.gtc.social.domain.User;

import java.util.Date;

public interface ForbiddenService {
    /**
     * 封号，如果当前时间内已经被封号或者存在永久封号，会失败
     * @param days 0 表示永久封号
     */
    int forbidden(User user, int days, String message) throws ApiException;

    /**
     * 取消封号 取消所有已经封号的记录
     */
    int disableForbidden(User user) throws ApiException;

    /**
     * 获取用户当前封号，如果没有，返回null
     */
    Forbidden getUserForbidden(User user) throws ApiException;
}
