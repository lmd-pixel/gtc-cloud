package com.fmisser.fpp.scheduler.quartz.conf;

import com.fmisser.fpp.scheduler.quartz.prop.QuartzDataSourceProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author by fmisser
 * @create 2021/5/17 6:52 下午
 * @description
 */

@Configuration
@EnableConfigurationProperties(QuartzDataSourceProperty.class)
public class QuartzDataSourcePropertyConfig {
}
