package com.fmisser.fpp.oss.minio.service;

import com.fmisser.fpp.oss.abs.service.OssService;
import com.fmisser.fpp.oss.minio.prop.MinioProp;
import io.minio.*;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.util.List;

@Service
public class MinioOssService implements OssService {

    private static MinioClient minioClient;
    private final MinioProp minioProp;

    public MinioOssService(MinioProp minioProp) {
        this.minioProp = minioProp;
    }

    @PostConstruct
    public void init() {
        minioClient = MinioClient.builder()
                .endpoint(minioProp.getMinioUrl())
                .credentials(minioProp.getMinioAccessKey(), minioProp.getMinioSecretKey())
                .build();
    }

    @Override
    public String getName() throws RuntimeException {
//        return "minio";
        return minioProp.getMinioVisitUrl() + minioProp.getGiftPrefix();
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
