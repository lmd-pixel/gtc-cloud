package com.fmisser.fpp.oss.cos.service.impl;

import com.fmisser.fpp.oss.cos.service.CosService;
import com.qcloud.cos.COSClient;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
    private final String APP_ID = "";
    
    private final COSClient cosClient;

    @Override
    public String getName() throws RuntimeException {
        return "tencent-cos";
    }

    @Override
    public String createBucket(String name) throws RuntimeException {
        return null;
    }

    @Override
    public String deleteBucket(String name) throws RuntimeException {
        return null;
    }

    @Override
    public Boolean isBucketExist(String name) throws RuntimeException {
        return null;
    }

    @Override
    public <T> List<T> getAllBucket() throws RuntimeException {
        return null;
    }

    @Override
    public String putObject(String bucketName, String objectName, String fileName, String contentType) throws RuntimeException {
        return null;
    }

    @Override
    public String putObject(String bucketName, String objectName, InputStream inputStream, Long size, String contentType) throws RuntimeException {
        return null;
    }

    @Override
    public String delObject(String bucketName, String objectName) throws RuntimeException {
        return null;
    }

    @Override
    public String getObject(String bucketName, String objectName) throws RuntimeException {
        return null;
    }

    @Override
    public String getDomainName(String bucket) throws RuntimeException {
        String region = cosClient.getClientConfig().getRegion().getRegionName();
        return String.format("%s-%s.cos.%s.myqcloud.com", bucket, APP_ID, region);
    }
}
