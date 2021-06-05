package com.fmisser.gtc.push;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * @author by fmisser
 * @create 2021/5/25 12:34 下午
 * @description TODO
 */

@SpringBootApplication(scanBasePackages = {"com.fmisser.gtc.push", "com.fmisser.gtc.base", "com.fmisser.fpp.*"})
@EnableEurekaClient
public class PushApplication {
    public static void main(String[] args) {
        SpringApplication.run(PushApplication.class, args);
    }
}
