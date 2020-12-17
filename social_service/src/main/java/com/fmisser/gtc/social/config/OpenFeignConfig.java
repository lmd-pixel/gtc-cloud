package com.fmisser.gtc.social.config;

import feign.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * feign配置
 */

@Configuration
public class OpenFeignConfig {
    // feign 日记输出 , 配置文件中也需要配置logging： logging.level.com.fmisser.gtc.social.feign.*=debug
    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }
}
