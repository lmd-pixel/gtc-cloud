package com.fmisser.gtc.social.service.impl;

import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.social.domain.User;
import com.fmisser.gtc.social.service.AsyncService;
import com.fmisser.gtc.social.service.ImService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * @author by fmisser
 * @create 2021/5/22 11:36 上午
 * @description 异步任务实现类
 */

@Slf4j
@Service
@AllArgsConstructor
public class AsyncServiceImpl implements AsyncService {

    private final ImService imService;

    @Async("async-task-exec")
    @Override
    public CompletableFuture<Integer> setProfileAsync(User user, Long delayMills) throws ApiException {
        if (delayMills > 0) {
            try {
                Thread.sleep(5000L);
            } catch (InterruptedException e) {
                log.error("[async] set profile async exception when thread sleep...");
                throw new ApiException(-1, "内部异常");
            }
        }

        return CompletableFuture.completedFuture(imService.setProfile(user));
    }
}
