package com.fmisser.fpp.thirdparty.jpush.conf;

import com.fmisser.fpp.thirdparty.jpush.prop.JPushExtensionProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(JPushExtensionProperty.class)
public class JPushExtensionPropertyConfig {
}
