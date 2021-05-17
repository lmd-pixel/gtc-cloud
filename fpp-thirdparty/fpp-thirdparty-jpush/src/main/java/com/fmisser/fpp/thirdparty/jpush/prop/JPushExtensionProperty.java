package com.fmisser.fpp.thirdparty.jpush.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * @author fmisser
 * @create 2021-05-13 下午10:40
 * @description jpush 属性扩展
 */

@Data
@Configuration
@EnableConfigurationProperties(JPushExtensionProperty.class)
@ConfigurationProperties(prefix = "fpp.jpush.ext", ignoreInvalidFields = true)
public class JPushExtensionProperty {
    private Map<String, JPushAppProperty> apps;
}
