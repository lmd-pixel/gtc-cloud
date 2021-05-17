package com.fmisser.fpp.mq.rabbit.conf;

import com.fmisser.fpp.mq.rabbit.prop.RabbitExtensionProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author by fmisser
 * @create 2021/5/17 6:50 下午
 * @description
 */

@Configuration
@EnableConfigurationProperties(RabbitExtensionProperty.class)
public class RabbitExtensionPropertyConfig {
}
