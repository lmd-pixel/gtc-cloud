package com.fmisser.gtc.social.service;

import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.social.domain.User;

public interface BlockService {
    // 屏蔽某条动态
    int blockOneDynamic(User user, User dstUser, Long dynamicId, int isBlock) throws ApiException;

    // 屏蔽某个人的所有动态
    int blockUserDynamic(User user, User dstUser, int isBlock) throws ApiException;

    // 拉黑某个人
    int blockUser(User user, User dstUser, int isBlock) throws ApiException;
}
