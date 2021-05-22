package com.fmisser.gtc.social.service;

import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.social.domain.User;
import java.util.concurrent.CompletableFuture;

/**
 * @author by fmisser
 * @create 2021/5/22 11:34 上午
 * @description 异步服务
 */
public interface AsyncService {
    CompletableFuture<Integer> setProfileAsync(User user, Long delayMills) throws ApiException;
}
