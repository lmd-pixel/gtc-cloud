package com.fmisser.fpp.thirdparty.jpush.conf;

/**
 * @author fmisser
 * @create 2021-05-11 下午5:37
 * @description
 */

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

/**
 * @author fmisser
 * @create 2021-05-11 下午5:29
 * @description feign 配置
 */
@Configuration
@EnableFeignClients(basePackages = "com.fmisser.fpp.thirdparty.jpush")
public class JPushFeignConfig {
}
