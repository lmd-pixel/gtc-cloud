package com.fmisser.fpp.mq.rabbit.prop;

import com.fmisser.fpp.mq.rabbit.conf.RabbitExchangeProperty;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Data
@Configuration
@EnableConfigurationProperties(RabbitProp.class)
@ConfigurationProperties(prefix = "fpp.mq.rabbit", ignoreInvalidFields = true)
public class RabbitProp {
    private Map<String, RabbitExchangeProperty> exchanges;
}
