package com.fmisser.fpp.oss.cos.conf;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

/**
 * @author by fmisser
 * @create 2021/7/9 2:43 下午
 * @description TODO
 */

@Configuration
@EnableFeignClients(basePackages = "com.fmisser.fpp.oss.cos")
public class CosFeignConfig {
}
