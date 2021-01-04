package com.fmisser.gtc.social.service;

import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.social.domain.Gift;
import com.fmisser.gtc.social.domain.User;

import java.util.List;

public interface GiftService {
    // 获取礼物列表
    List<Gift> getGiftList() throws ApiException;

    // 送礼物
    int postGift(User fromUser, User toUser, Long giftId, Integer count) throws ApiException;
}
