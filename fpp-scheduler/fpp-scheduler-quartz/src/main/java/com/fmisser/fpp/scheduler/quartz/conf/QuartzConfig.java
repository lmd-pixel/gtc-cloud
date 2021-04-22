package com.fmisser.fpp.scheduler.quartz.conf;

import com.fmisser.fpp.scheduler.quartz.prop.QuartzDataSourceProperty;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.quartz.QuartzProperties;
import org.springframework.boot.autoconfigure.quartz.SchedulerFactoryBeanCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * @author fmisser
 * @create 2021-04-22 下午3:58
 * @description quartz 自定义配置
 */

@EnableAutoConfiguration
@Configuration
@AllArgsConstructor
public class QuartzConfig {
    private final QuartzDataSourceProperty quartzDataSourceProperty;

    @Bean
    public SchedulerFactoryBeanCustomizer schedulerFactoryBeanCustomizer(
            QuartzProperties quartzProperties) {
        return new SchedulerFactoryBeanCustomizer() {
            @Override
            public void customize(SchedulerFactoryBean schedulerFactoryBean) {
                // 添加自定义属性
                Properties properties = new Properties();
                properties.putAll(quartzProperties.getProperties());
                schedulerFactoryBean.setQuartzProperties(properties);

                // 自定义数据源
                if (quartzDataSourceProperty.getIsolate()) {
                    DataSource dataSource = quartzDataSourceProperty.getDatasource()
                            .initializeDataSourceBuilder().build();
                    schedulerFactoryBean.setDataSource(dataSource);
                }
            }
        };
    }
}
