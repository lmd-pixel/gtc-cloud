package com.fmisser.gtc.base.prop;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Data
@Configuration
@PropertySource(value = {
        "classpath:config/oss-conf.properties",
        "classpath:config/oss-conf-${spring.profiles.active}.properties"},
        encoding = "UTF-8")
public class OssConfProp {

    @Deprecated
    @Value("${oss.minio.url}")
    private String minioUrl;

    @Deprecated
    @Value("${oss.minio.visit.url}")
    private String minioVisitUrl;

    @Deprecated
    @Value("${oss.minio.access_key}")
    private String minioAccessKey;

    @Deprecated
    @Value("${oss.minio.secret_key}")
    private String minioSecretKey;

    @Value("${oss.minio.object.aes_key}")
    private String objectAesKey;

    @Deprecated
    @Value("${oss.minio.bucket.user_profiles}")
    private String userProfileBucket;

    @Deprecated
    @Value("${user_profile.voice.prefix}")
    private String userProfileVoicePrefix;

    @Deprecated
    @Value("${user_profile.head.prefix}")
    private String userProfileHeadPrefix;

    @Deprecated
    @Value("${user_profile.photo.prefix}")
    private String userProfilePhotoPrefix;

    @Deprecated
    @Value("${user_profile.verify_image.prefix}")
    private String userProfileVerifyImagePrefix;

    @Deprecated
    @Value("${user_profile.video.prefix}")
    private String userProfileVideoPrefix;

    @Deprecated
    @Value("${oss.minio.bucket.user_dynamic}")
    private String userDynamicBucket;

    @Deprecated
    @Value("${user_dynamic.video.prefix}")
    private String userDynamicVideoPrefix;

    @Deprecated
    @Value("${user_dynamic.image.prefix}")
    private String userDynamicPicturePrefix;

    @Deprecated
    @Value("${oss.minio.bucket.system_config}")
    private String systemConfigBucket;

    @Deprecated
    @Value("${gift.prefix}")
    private String giftPrefix;

    @Deprecated
    @Value("${banner.prefix}")
    private String bannerPrefix;

    @Value("${oss.cos.bucket.user_dynamic}")
    private String userDynamicCosBucket;

    @Value("${oss.cos.cdn}")
    private String cosCdn;

    @Value("${cos.user_profile.voice.prefix}")
    private String cosUserProfileVoicePrefix;

    @Value("${cos.user_profile.head.prefix}")
    private String cosUserProfileHeadPrefix;

    @Value("${cos.user_profile.photo.prefix}")
    private String cosUserProfilePhotoPrefix;

    @Value("${cos.user_profile.verify_image.prefix}")
    private String cosUserProfileVerifyImagePrefix;

    @Value("${cos.user_profile.video.prefix}")
    private String cosUserProfileVideoPrefix;

    @Value("${cos.user_profile.video.thumbnail_prefix}")
    private String userProfileVideoThumbnailPrefix;

    @Value("${cos.user_dynamic.video.prefix}")
    private String cosUserDynamicVideoPrefix;

    @Value("${cos.user_dynamic.video.thumbnail_prefix}")
    private String userDynamicVideoThumbnailPrefix;

    @Value("${cos.user_dynamic.image.prefix}")
    private String cosUserDynamicPicturePrefix;

    @Value("${cos.system_config.gift.prefix}")
    private String cosGiftPrefix;

    @Value("${cos.system_config.banner.prefix}")
    private String cosBannerPrefix;

    @Value("${cos.user_profile.root_path}")
    private String cosUserProfileRootPath;

    @Value("${cos.user_dynamic.root_path}")
    private String cosUserDynamicRootPath;

    @Value("${cos.system_config.root_path}")
    private String cosSystemConfigRootPath;



}
