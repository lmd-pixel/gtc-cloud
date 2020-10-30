package com.fmisser.gtc.social.utils;

import io.minio.*;
import io.minio.messages.Bucket;
import lombok.SneakyThrows;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.util.List;

/**
 * Minio 简单封装
 */
public class MinioUtils {
    private static String MINIO_URL = "http://localhost:9000";
    private static String MINIO_ACCESSKEY = "root";
    private static String MINIO_SECRETKEY = "gtc_cloud@123456";
    private static String MINIO_BUCKET = "test-bucket";

    private static MinioClient minioClient;

    @PostConstruct
    public static void init() {
        minioClient = MinioClient.builder()
                .endpoint(MINIO_URL)
                .credentials(MINIO_ACCESSKEY, MINIO_SECRETKEY)
                .build();
    }

    @SneakyThrows(Exception.class)
    public static boolean bucketExists(String bucketName) {
        return minioClient.bucketExists(
                BucketExistsArgs.builder().bucket(bucketName).build());
    }

    @SneakyThrows(Exception.class)
    public static void createBucket(String bucketName) {
        boolean isExist = bucketExists(bucketName);
        if (!isExist) {
            minioClient.makeBucket(MakeBucketArgs.builder()
                    .bucket(bucketName).build());
        }
    }

    @SneakyThrows(Exception.class)
    public static List<Bucket> getAllBuckets() {
        return minioClient.listBuckets();
    }

    @SneakyThrows(Exception.class)
    public static ObjectWriteResponse upload(String bucketName, String objectName, String filename) {
        UploadObjectArgs args = UploadObjectArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .filename(filename)
//                .contentType("video/mp4")
                .build();
        return minioClient.uploadObject(args);
    }

    @SneakyThrows(Exception.class)
    public static ObjectWriteResponse put(String bucketName, String objectName, InputStream inputStream) {
        PutObjectArgs args = PutObjectArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .stream(inputStream, -1, -1)
//                .contentType("video/mp4")
                .build();
        return minioClient.putObject(args);
    }

    @SneakyThrows
    public static void delete(String bucketName, String objectName) {
        RemoveObjectArgs args = RemoveObjectArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .build();
        minioClient.removeObject(args);
    }

    @SneakyThrows
    public static String getObjectUrl(String bucketName, String objectName) {
        return minioClient.getObjectUrl(bucketName, objectName);
    }
}
