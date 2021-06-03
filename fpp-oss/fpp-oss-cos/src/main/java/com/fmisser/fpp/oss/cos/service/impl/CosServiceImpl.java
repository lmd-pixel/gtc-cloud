package com.fmisser.fpp.oss.cos.service.impl;

import com.fmisser.fpp.oss.cos.conf.CosDefine;
import com.fmisser.fpp.oss.cos.service.CosService;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.InputStream;
import java.util.List;

/**
 * @author by fmisser
 * @create 2021/5/28 8:19 下午
 * @description 腾讯对象存储 cos
 */
@Slf4j
@Service
@AllArgsConstructor
public class CosServiceImpl implements CosService {
    private final COSClient cosClient;

    @Override
    public String getName() throws RuntimeException {
        return "tencent-cos";
    }

    @Override
    public String createBucket(String name) throws RuntimeException {
        CreateBucketRequest request = new CreateBucketRequest(name);
        request.setCannedAcl(CannedAccessControlList.PublicRead);   // 公有读，似有写
        Bucket bucket = cosClient.createBucket(request);
        return bucket.getName();
    }

    @Override
    public String deleteBucket(String name) throws RuntimeException {
        DeleteBucketRequest request = new DeleteBucketRequest(name);
        cosClient.deleteBucket(request);
        return name;
    }

    @Override
    public Boolean isBucketExist(String name) throws RuntimeException {
        return cosClient.doesBucketExist(name);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> List<T> getAllBucket() throws RuntimeException {
        return (List<T>) cosClient.listBuckets();
    }

    @Override
    public String putObject(String bucketName, String objectName, String fileName, String contentType) throws RuntimeException {
        File file = new File(fileName);
        PutObjectRequest request = new PutObjectRequest(bucketName, objectName, file);
        PutObjectResult result = cosClient.putObject(request);
        return objectName;
    }

    @Override
    public String putObject(String bucketName, String objectName, InputStream inputStream, Long size, String contentType) throws RuntimeException {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(contentType);
        PutObjectRequest request = new PutObjectRequest(bucketName, objectName, inputStream, metadata);
        PutObjectResult result = cosClient.putObject(request);
        return objectName;

    }

    @Override
    public String delObject(String bucketName, String objectName) throws RuntimeException {
        DeleteObjectRequest request = new DeleteObjectRequest(bucketName, objectName);
        cosClient.deleteObject(request);
        return objectName;
    }

    @Override
    public String getObject(String bucketName, String objectName) throws RuntimeException {
        GetObjectRequest request = new GetObjectRequest(bucketName, objectName);
        COSObject object = cosClient.getObject(request);
        return object.toString();
    }

    @Override
    public String getDomainName(String cdn, String bucket) throws RuntimeException {
        // 如果配置了cdn 则直接走cdn域名，没有的话则走cos风格域名
        if (StringUtils.isEmpty(cdn)) {
            return String.format("https://%s.cos.%s.myqcloud.com", bucket, CosDefine.COS_REGION);
        } else {
            return cdn;
        }

    }
}
