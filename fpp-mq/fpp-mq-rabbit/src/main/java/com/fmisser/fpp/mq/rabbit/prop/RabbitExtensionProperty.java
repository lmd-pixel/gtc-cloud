package com.fmisser.fpp.mq.rabbit.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * @author fmisser
 * @create 2021-04-10 下午2:09
 * @description 新增自定义配置用于定义交换机，队列，绑定关系 ，不影响使用@Bean方式定义
 */

@Data
@Configuration
@EnableConfigurationProperties(RabbitExtensionProperty.class)
@ConfigurationProperties(prefix = "fpp.rabbitmq.ext", ignoreInvalidFields = true)
public class RabbitExtensionProperty {
    private Map<String, RabbitBindingProperty> bindings;
}
