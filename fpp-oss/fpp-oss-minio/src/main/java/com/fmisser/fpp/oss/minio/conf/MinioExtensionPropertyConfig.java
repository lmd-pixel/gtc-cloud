package com.fmisser.fpp.oss.minio.conf;

import com.fmisser.fpp.oss.minio.prop.MinioExtensionProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author by fmisser
 * @create 2021/5/17 6:53 下午
 * @description
 */

@Configuration
@EnableConfigurationProperties(MinioExtensionProperty.class)
public class MinioExtensionPropertyConfig {
}
