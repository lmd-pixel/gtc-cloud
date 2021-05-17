package com.fmisser.fpp.scheduler.quartz.prop;

import lombok.Data;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author fmisser
 * @create 2021-04-22 下午4:29
 * @description 自定义数据源配置
 *
 * 一种方式通过spring quartz 提供的 @QuartzDataSource 注解（其实是个@Qualifier）去定一个新的Bean，例如:
 *
 *     @Bean
 *     @QuartzDataSource
 *     @ConfigurationProperties("spring.datasource.quartz")
 *     public DataSourceProperties schedulerDBProperties() {
 *         return new DataSourceProperties();
 *     }
 *
 *     @Bean
 *     @QuartzDataSource
 *     public DataSource quartzDataSource() {
 *         return schedulerDBProperties().initializeDataSourceBuilder().build();
 *     }
 *
 * 这种方式麻烦的地方在于还需要再定一个 增加了@Primary注解的原始数据源，例如:
 *
 *     @Bean
 *     @Primary
 *     public DataSource dataSource() {
 *         return primaryDBProperties().initializeDataSourceBuilder().build();
 *     }
 *
 *     @Bean
 *     @Primary
 *     @ConfigurationProperties("spring.datasource")
 *     public DataSourceProperties primaryDBProperties() {
 *         return new DataSourceProperties();
 *     }
 *
 * 第二种自定义数据源参数，然后通过quartz 自定义配置指定该数据源,这里使用第二种方式
 *
 * 创建数据库表建议手动创建
 * 从quartz项目中找到初始化脚本：classpath:org/quartz/impl/jdbcjobstore/tables_mysql_innodb.sql
 */
@Data
@ConfigurationProperties(prefix = "spring.quartz")
public class QuartzDataSourceProperty {
    // 数据源配置
    private DataSourceProperties datasource;
    // 是否使用单独的数据源
    private Boolean isolate = false;
}
