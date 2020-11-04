package com.fmisser.gtc.notice;

import com.fmisser.gtc.notice.config.FeignClientInterceptor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication(scanBasePackages = {"com.fmisser.gtc.notice", "com.fmisser.gtc.base"})
@EnableEurekaClient
@EnableFeignClients(defaultConfiguration = FeignClientInterceptor.class)
@EnableJpaAuditing
public class NoticeApplication {

    public static void main(String[] args) {
        SpringApplication.run(NoticeApplication.class, args);
    }

}
