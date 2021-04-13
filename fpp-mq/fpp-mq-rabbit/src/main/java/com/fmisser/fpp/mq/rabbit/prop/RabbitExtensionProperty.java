package com.fmisser.fpp.mq.rabbit.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * @author fmisser
 * @create 2021-04-10 下午2:09
 * @description
 */

@Data
@Configuration
@EnableConfigurationProperties(RabbitExtensionProperty.class)
@ConfigurationProperties(prefix = "fpp.rabbitmq.ext", ignoreInvalidFields = true)
public class RabbitExtensionProperty {
    private Map<String, RabbitBindingProperty> bindings;
}
