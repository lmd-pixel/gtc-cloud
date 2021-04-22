package com.fmisser.fpp.oss.minio.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author fmisser
 * @create 2021-04-22 下午9:32
 * @description minio 配置
 */

@Data
@Configuration
@EnableConfigurationProperties(MinioExtensionProperty.class)
@ConfigurationProperties(prefix = "fpp.minio.ext", ignoreInvalidFields = true)
public class MinioExtensionProperty {
    private String url;
    private String accessKey;
    private String secretKey;
}
