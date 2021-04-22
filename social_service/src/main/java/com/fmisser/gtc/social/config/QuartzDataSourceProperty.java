package com.fmisser.gtc.social.config;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.quartz.QuartzProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author fmisser
 * @create 2021-04-22 下午4:38
 * @description
 */
@Deprecated
//@EqualsAndHashCode(callSuper = true)
//@Data
//@Configuration
//@EnableConfigurationProperties(QuartzDataSourceProperty.class)
//@ConfigurationProperties(prefix = "spring.quartz")
public class QuartzDataSourceProperty {
    private DataSourceProperties datasource;
    private Boolean isolate = false;
}
