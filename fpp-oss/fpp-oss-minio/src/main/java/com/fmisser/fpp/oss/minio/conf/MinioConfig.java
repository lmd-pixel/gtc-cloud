package com.fmisser.fpp.oss.minio.conf;

import com.fmisser.fpp.oss.minio.prop.MinioExtensionProperty;
import io.minio.MinioClient;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author fmisser
 * @create 2021-04-22 下午10:13
 * @description minio 配置
 */

@Configuration
@AllArgsConstructor
public class MinioConfig {
    private final MinioExtensionProperty minioExtensionProperty;

    @Bean
    MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(minioExtensionProperty.getUrl())
                .credentials(minioExtensionProperty.getAccessKey(),
                        minioExtensionProperty.getSecretKey())
                .build();
    }
}
