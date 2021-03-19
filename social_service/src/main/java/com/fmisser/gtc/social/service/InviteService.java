package com.fmisser.gtc.social.service;

import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.social.domain.User;

public interface InviteService {
    // 邀请人通过自己的 digit id 邀请
    int inviteFromDigitId(User user, String inviteCode) throws ApiException;

    // 获取邀请人的信息，邀请码为用户 digit id
    User getInviteUserFromDigitId(String inviteCode) throws ApiException;
}
