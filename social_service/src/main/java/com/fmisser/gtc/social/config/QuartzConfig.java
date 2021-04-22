package com.fmisser.gtc.social.config;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.quartz.QuartzDataSource;
import org.springframework.boot.autoconfigure.quartz.QuartzProperties;
import org.springframework.boot.autoconfigure.quartz.SchedulerFactoryBeanCustomizer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * @author fmisser
 * @create 2021-04-17 下午5:56
 * @description
 * @deprecated 通过单独的quartz 模块处理
 */
@Deprecated
//@Configuration
public class QuartzConfig {

//    private final QuartzDataSourceProperty quartzDataSourceProperty;
//
//    public QuartzConfig(QuartzDataSourceProperty quartzDataSourceProperty) {
//        this.quartzDataSourceProperty = quartzDataSourceProperty;
//    }


//    @Bean
//    @Primary
//    public DataSource dataSource() {
//        return primaryDBProperties().initializeDataSourceBuilder().build();
//    }
//
//    @Bean
//    @Primary
//    @ConfigurationProperties("spring.datasource")
//    public DataSourceProperties primaryDBProperties() {
//        return new DataSourceProperties();
//    }
////
//    @Bean
//    @QuartzDataSource
//    @ConfigurationProperties("spring.datasource.quartz")
//    public DataSourceProperties schedulerDBProperties() {
//        return new DataSourceProperties();
//    }
//
//    @Bean
//    @QuartzDataSource
//    public DataSource quartzDataSource() {
//        return schedulerDBProperties().initializeDataSourceBuilder().build();
//    }

//    @Bean
//    public SchedulerFactoryBeanCustomizer schedulerFactoryBeanCustomizer(
//            QuartzProperties quartzProperties) {
//        return new SchedulerFactoryBeanCustomizer() {
//            @Override
//            public void customize(SchedulerFactoryBean schedulerFactoryBean) {
//                Properties properties = new Properties();
//                properties.putAll(quartzProperties.getProperties());
//                schedulerFactoryBean.setQuartzProperties(properties);
//
//                if (quartzDataSourceProperty.getIsolate()) {
//                    DataSource dataSource = quartzDataSourceProperty.getDatasource().initializeDataSourceBuilder().build();
//                    schedulerFactoryBean.setDataSource(dataSource);
//                }
//            }
//        };
//    }
}
