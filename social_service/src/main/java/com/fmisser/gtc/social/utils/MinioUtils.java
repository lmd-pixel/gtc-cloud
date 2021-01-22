package com.fmisser.gtc.social.utils;

import com.fmisser.gtc.base.prop.OssConfProp;
import io.minio.*;
import io.minio.messages.Bucket;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.util.List;

/**
 * Minio 简单封装
 */

// TODO: 2021/1/21 改成service

@Component
public class MinioUtils {

    private final OssConfProp ossConfProp;

    private static MinioClient minioClient;

    public MinioUtils(OssConfProp ossConfProp) {
        this.ossConfProp = ossConfProp;
    }

    @PostConstruct
    public void init() {
        minioClient = MinioClient.builder()
                .endpoint(ossConfProp.getMinioUrl())
                .credentials(ossConfProp.getMinioAccessKey(), ossConfProp.getMinioSecretKey())
                .build();
    }

    @SneakyThrows(Exception.class)
    public boolean bucketExists(String bucketName) {
        return minioClient.bucketExists(
                BucketExistsArgs.builder().bucket(bucketName).build());
    }

    @SneakyThrows(Exception.class)
    public void createBucket(String bucketName) {
        boolean isExist = bucketExists(bucketName);
        if (!isExist) {
            minioClient.makeBucket(MakeBucketArgs.builder()
                    .bucket(bucketName).build());
        }
    }

    @SneakyThrows(Exception.class)
    public List<Bucket> getAllBuckets() {
        return minioClient.listBuckets();
    }

    @SneakyThrows(Exception.class)
    public ObjectWriteResponse upload(String bucketName, String objectName, String filename, String contentType) {
        UploadObjectArgs args = UploadObjectArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .filename(filename)
                .contentType(contentType)
//                .contentType("video/mp4")
                .build();
        return minioClient.uploadObject(args);
    }

    @SneakyThrows(Exception.class)
    public ObjectWriteResponse put(String bucketName, String objectName,
                                          InputStream inputStream, long size, String contentType) {
        PutObjectArgs args = PutObjectArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .stream(inputStream, size, -1)
                .contentType(contentType)
                .build();
        return minioClient.putObject(args);
    }

    @SneakyThrows
    public void delete(String bucketName, String objectName) {
        RemoveObjectArgs args = RemoveObjectArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .build();
        minioClient.removeObject(args);
    }

    @SneakyThrows
    public String getObjectUrl(String bucketName, String objectName) {
        return minioClient.getObjectUrl(bucketName, objectName);
    }
}
