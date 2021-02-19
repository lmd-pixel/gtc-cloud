package com.fmisser.gtc.social.service;

import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.social.domain.Gift;
import com.fmisser.gtc.social.domain.User;

import java.util.Map;

/**
 * im trtc 相关 服务
 */

public interface ImService {
    // 用户登陆
    String login(User user) throws ApiException;

    // 用户登出
    int logout(User user) throws ApiException;

    // 发起通话，返回需要的数据
    Long call(User userFrom, User userTo, int type) throws ApiException;

    // 接受通话
    int accept(User userFrom, User userTo, Long roomId) throws ApiException;

    // 挂断通话
    Map<String, Object> hangup(User user, Long roomId) throws ApiException;

    // 更新通话并计费
    Map<String, Object> updateCall(User user, Long roomId) throws ApiException;

    // 生成 user sig
    String genUserSign(User user) throws ApiException;

    // send to user
    int sendToUser(User fromUser, User toUser, String content) throws ApiException;

    int sendGiftMsg(User userFrom, User userTo, Gift gift, int count) throws ApiException;

    int sendAfterSendMsg(User userFrom, User userTo, int tag, int coin, int card) throws ApiException;
}
