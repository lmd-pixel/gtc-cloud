package com.fmisser.fpp.cache.redis.service.impl;

import com.fmisser.fpp.cache.redis.service.RedisService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author fmisser
 * @create 2021-05-10 下午2:28
 * @description
 */
@Slf4j
@Service
@AllArgsConstructor
public class RedisServiceImpl implements RedisService {
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public boolean expire(String key, long ttl) throws RuntimeException {
        Boolean ret = redisTemplate.expire(key, ttl, TimeUnit.SECONDS);
        if (Objects.isNull(ret)) {
            log.error("redis template expire return null!");
            throw new RuntimeException("redis template expire return null!");
        }

        return ret;
    }

    @Override
    public long getExpire(String key) throws RuntimeException {
        Long ret = redisTemplate.getExpire(key, TimeUnit.SECONDS);
        if (Objects.isNull(ret)) {
            log.error("redis template get expire return null!");
            throw new RuntimeException("redis template get expire return null!");
        }

        return ret;
    }

    @Override
    public boolean hasKey(String key) throws RuntimeException {
        Boolean ret = redisTemplate.hasKey(key);
        if (Objects.isNull(ret)) {
            log.error("redis template has key return null!");
            throw new RuntimeException("redis template has key return null!");
        }
        return ret;
    }

    @Override
    public boolean delKey(String key) throws RuntimeException {
        return delKeys(Collections.singletonList(key));
    }

    @Override
    public boolean delKeys(List<String> keys) throws RuntimeException {
        Long ret = redisTemplate.delete(keys);
        if (Objects.isNull(ret)) {
            log.error("redis template del keys return null!");
            throw new RuntimeException("redis template del keys return null!");
        }

        if (ret != keys.size()) {
            // 可能部分key已删除或者已过期，这里不抛出异常
            log.warn("redis template del keys exception: some of keys may not deleted!");
            return false;
        }

        return true;
    }

    @Override
    public Object get(String key) throws RuntimeException {
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public boolean set(String key, Object value) throws RuntimeException {
        redisTemplate.opsForValue().set(key, value);
        return true;
    }

    @Override
    public boolean set(String key, Object value, long ttl) throws RuntimeException {
        redisTemplate.opsForValue().set(key, value, ttl, TimeUnit.SECONDS);
        return true;
    }

    @Override
    public long listLeftPush(String key, Object... values) throws RuntimeException {
        Long ret = redisTemplate.opsForList().leftPushAll(key, values);

        if (Objects.isNull(ret)) {
            log.error("redis template list left push keys return null!");
            throw new RuntimeException("redis template list left push keys return null!");
        }

        return ret;
    }

    @Override
    public Object listRightPop(String key) throws RuntimeException {
        return redisTemplate.opsForList().rightPop(key);
    }
}
