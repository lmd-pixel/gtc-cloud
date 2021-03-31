package com.fmisser.gtc.social.service;


import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.social.domain.User;

/**
 * 活跃数据存储
 * todo: 使用nosql处理相关逻辑
 */

public interface ActiveService {
    int inviteCall(User user, Long roomId) throws ApiException;
    int cancelCall(User user, Long roomId) throws ApiException;
    int timeoutCall(User user, Long roomId) throws ApiException;
    int rejectCall(User user, Long roomId) throws ApiException;
    int acceptCall(User user, Long roomId) throws ApiException;
    int endCall(User user, Long roomId) throws ApiException;
    boolean isCallBusy(User user) throws ApiException;
}
