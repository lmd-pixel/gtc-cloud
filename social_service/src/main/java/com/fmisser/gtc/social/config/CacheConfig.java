package com.fmisser.gtc.social.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class CacheConfig extends CachingConfigurerSupport {

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        RedisCacheConfiguration defaultCacheConfig =
                RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofDays(1));

        RedisCacheWriter redisCacheWriter =
                RedisCacheWriter.nonLockingRedisCacheWriter(redisConnectionFactory);

        // 自定义cache ttl配置
//        Map<String, RedisCacheConfiguration> customCacheConfig = new HashMap<>();
//        customCacheConfig.put("anchorList",
//                RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofSeconds(30)));
//
//        RedisCacheManager redisCacheManager = RedisCacheManager.builder()
//                .cacheDefaults(defaultCacheConfig)
//                .withInitialCacheConfigurations(customCacheConfig)
//                .cacheWriter(redisCacheWriter)
//                .build();

        CommonRedisCacheManager redisCacheManager =
                new CommonRedisCacheManager(redisCacheWriter, defaultCacheConfig);

        return redisCacheManager;
    }

}
