package com.fmisser.gtc.social.service;

import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.social.domain.User;

public interface GreetService {
    // 创建骚扰
    int createGreet(User user) throws ApiException;

    int updateGreet(User user, int stage) throws ApiException;

    // 今天的骚扰是否存在
    boolean isTodayGreetExist(User user) throws ApiException;

    // 用户今天是否已经回复
    boolean isTodayReply(User user) throws ApiException;

    int userReplyToday(User userFrom, User userTo, Long replyMsgId) throws ApiException;
}
