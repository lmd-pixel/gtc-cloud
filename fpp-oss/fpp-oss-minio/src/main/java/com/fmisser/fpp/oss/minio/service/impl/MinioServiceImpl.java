package com.fmisser.fpp.oss.minio.service.impl;

import com.fmisser.fpp.oss.minio.service.MinioService;
import io.minio.*;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.List;

@Service
@AllArgsConstructor
public class MinioServiceImpl implements MinioService {
    private final MinioClient minioClient;

    @Override
    public String getName() throws RuntimeException {
        return "oss-minio";
    }

    @SneakyThrows
    @Override
    public String createBucket(String name) throws RuntimeException {
        boolean isExist = isBucketExist(name);
        if (!isExist) {
            minioClient.makeBucket(MakeBucketArgs.builder()
                    .bucket(name).build());
        }

        return name;
    }

    @SneakyThrows
    @Override
    public String deleteBucket(String name) throws RuntimeException {
        RemoveBucketArgs args = RemoveBucketArgs.builder()
                .bucket(name).build();
        minioClient.removeBucket(args);

        return name;
    }

    @SneakyThrows
    @Override
    public Boolean isBucketExist(String name) throws RuntimeException {
        BucketExistsArgs args = BucketExistsArgs.builder()
                .bucket(name).build();
        return minioClient.bucketExists(args);
    }

    @SneakyThrows
    @Override
    @SuppressWarnings("unchecked")
    public <T> List<T> getAllBucket() throws RuntimeException {
        ListBucketsArgs args = ListBucketsArgs.builder()
                .build();
        return (List<T>) minioClient.listBuckets(args);
    }

    @SneakyThrows
    @Override
    public String pubObject(String bucketName,
                            String objectName,
                            String fileName,
                            String contentType) throws RuntimeException {
        UploadObjectArgs args = UploadObjectArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .filename(fileName)
                .contentType(contentType)
                .build();
        ObjectWriteResponse response = minioClient.uploadObject(args);
        return response.object();
    }

    @SneakyThrows
    @Override
    public String pubObject(String bucketName,
                            String objectName,
                            InputStream inputStream,
                            Long size,
                            String contentType) throws RuntimeException {
        PutObjectArgs args = PutObjectArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .stream(inputStream, size, -1)
                .contentType(contentType)
                .build();
        ObjectWriteResponse response = minioClient.putObject(args);
        return response.object();
    }

    @SneakyThrows
    @Override
    public String delObject(String bucketName, String objectName) throws RuntimeException {
        RemoveObjectArgs args = RemoveObjectArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .build();
        minioClient.removeObject(args);

        return objectName;
    }

    @Override
    public String getObject(String bucketName, String objectName) throws RuntimeException {
        throw new UnsupportedOperationException();
    }
}
