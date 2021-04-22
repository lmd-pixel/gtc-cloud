package com.fmisser.fpp.oss.abs.service;

import java.io.InputStream;
import java.util.List;

/**
 * 对象存储服务
 */

public interface OssService {
    String getName() throws RuntimeException;
    String createBucket(String name) throws RuntimeException;
    String deleteBucket(String name) throws RuntimeException;
    Boolean isBucketExist(String name) throws RuntimeException;
    <T> List<T> getAllBucket() throws RuntimeException;
    String putObject(String bucketName,
                     String objectName,
                     String fileName,
                     String contentType) throws RuntimeException;
    String putObject(String bucketName,
                     String objectName,
                     InputStream inputStream,
                     Long size,
                     String contentType) throws RuntimeException;
    String delObject(String bucketName,
                     String objectName) throws RuntimeException;
    String getObject(String bucketName,
                     String objectName) throws RuntimeException;
}
