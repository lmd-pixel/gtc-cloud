package com.fmisser.gtc.base.prop;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Data
@Component
@PropertySource(value = "classpath:config/oss-conf.properties", encoding = "UTF-8")
public class OssConfProp {

    @Value("${oss.minio.url}")
    private String minioUrl;

    @Value("${oss.minio.access_key}")
    private String minioAccessKey;

    @Value("${oss.minio.secret_key}")
    private String minioSecretKey;

    @Value("${oss.minio.object.aes_key}")
    private String objectAesKey;

    @Value("${oss.minio.bucket.user_profiles}")
    private String userProfileBucket;

    @Value("${user_profile.voice.prefix}")
    private String userProfileVoicePrefix;

    @Value("${user_profile.head.prefix}")
    private String userProfileHeadPrefix;

    @Value("${user_profile.photo.prefix}")
    private String userProfilePhotoPrefix;

    @Value("${user_profile.verify_image.prefix}")
    private String userProfileVerifyImagePrefix;

    @Value("${oss.minio.bucket.user_dynamic}")
    private String userDynamicBucket;

    @Value("${user_dynamic.video.prefix}")
    private String userDynamicVideoPrefix;

    @Value("${user_dynamic.image.prefix}")
    private String userDynamicPicturePrefix;
}
