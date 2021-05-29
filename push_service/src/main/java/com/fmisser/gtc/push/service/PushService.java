package com.fmisser.gtc.push.service;

import com.fmisser.gtc.base.exception.ApiException;

/**
 * @author by fmisser
 * @create 2021/5/25 3:38 下午
 * @description TODO
 */
public interface PushService {
    // 广播消息
    void broadcastMsg(String content) throws ApiException;
}
