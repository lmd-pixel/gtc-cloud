package com.fmisser.gtc.social.service;

import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.social.domain.User;

/**
 * @author fmisser
 * @create 2021-05-10 下午3:39
 * @description 小助手消息服务
 */
public interface AssistService {
    void sendRechargeMsg(User user) throws ApiException;
}
