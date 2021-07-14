package com.fmisser.fpp.oss.cos.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fmisser.fpp.oss.cos.conf.CosDefine;
import com.fmisser.fpp.oss.cos.dto.RecognitionResult;
import com.fmisser.fpp.oss.cos.dto.VideoAuditQueryResponse;
import com.fmisser.fpp.oss.cos.dto.VideoAuditRequest;
import com.fmisser.fpp.oss.cos.dto.VideoAuditResponse;
import com.fmisser.fpp.oss.cos.feign.CosFeign;
import com.fmisser.fpp.oss.cos.service.CosService;
import com.fmisser.fpp.oss.cos.utils.COSSigner;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.http.HttpMethodName;
import com.qcloud.cos.model.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
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
    private final COSSigner cosSigner;
    private final COSCredentials cosCredentials;
    private final CosFeign cosFeign;
    private final XmlMapper xmlMapper;

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
        metadata.setHeader("Content-Type", contentType);
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
    public String getDomainName(String cdn, String bucketName) throws RuntimeException {
        // 如果配置了cdn 则直接走cdn域名，没有的话则走cos风格域名
        if (StringUtils.isEmpty(cdn)) {
            return String.format("https://%s.cos.%s.myqcloud.com", bucketName, CosDefine.COS_REGION);
        } else {
            return cdn;
        }
    }

    @Override
    public String getPictureAuditDomainName(String bucketName) throws RuntimeException {
        return String.format("https://%s.cos.%s.myqcloud.com", bucketName, CosDefine.COS_REGION);
    }

    @Override
    public String getVideoAuditDomainName(String bucketName) throws RuntimeException {
        return String.format("https://%s.ci.%s.myqcloud.com", bucketName, CosDefine.COS_REGION);
    }

    @Override
    public String getCosAuthString(HttpMethodName methodName, String resourcePath, Date expiredTime) throws RuntimeException {
        return cosSigner.buildAuthorizationStr(methodName, resourcePath, cosCredentials, expiredTime);
    }


    @Override
    public RecognitionResult recognizePicture(String host, String picturePath) throws RuntimeException {
        // TODO: 2021/7/14 配置相关引用的变量值，或改成参数由外部配置并传入
        String url = String.format("%s/%s?ci-process=sensitive-content-recognition&detect-type=porn,politics,ads" +
                        "&interval=48&max-frames=5&bizType=dymic_img",
                host, picturePath);

        try {
            Date now = new Date();
            Date expiredTime = new Date(now.getTime() + 3600 * 1000);
            String response = cosFeign.cosPictureAudit(new URI(url),
                    getCosAuthString(HttpMethodName.GET, "/" + picturePath, expiredTime));

            return xmlMapper.readValue(response, RecognitionResult.class);

        } catch (URISyntaxException e) {
            log.error("[async] create URI with url: {} exception: {}", url, e.getMessage());
            throw new RuntimeException("create url failed");
        } catch (JsonMappingException e) {
            log.error("[async] json mapper exception: {}", e.getMessage());
            throw new RuntimeException("xml mapping failed");
        } catch (JsonProcessingException e) {
            log.error("[async] json processing exception: {}", e.getMessage());
            throw new RuntimeException("xml processing failed");
        } catch (Exception e) {
            log.error("[async] request exception: {}", e.getMessage());
            throw new RuntimeException("request failed");
        }
    }

    @Override
    public VideoAuditQueryResponse recognizeVideo(String host, String videoPath) throws RuntimeException {
        // TODO: 2021/7/14 配置相关引用的变量值，或改成参数由外部配置并传入
        String auditUrl = String.format("%s/video/auditing", host);

        // create request object
        VideoAuditRequest request = new VideoAuditRequest();
        // input
        VideoAuditRequest.Input input = new VideoAuditRequest.Input();
        input.setObject(videoPath);
        request.setInput(input);
        // conf
        VideoAuditRequest.Conf conf = new VideoAuditRequest.Conf();
        conf.setDetectType("Porn,Terrorism,Politics,Ads");
        // snapshot
        VideoAuditRequest.Snapshot snapshot = new VideoAuditRequest.Snapshot();
        snapshot.setMode("Interval");
        snapshot.setTimeInterval("2");
        snapshot.setCount("5");
        conf.setSnapshot(snapshot);
        conf.setBizType("185d993ce2f439246ccba43b65379d12");
        conf.setDetectContent("0");
        request.setConf(conf);

        try {
            Date now = new Date();
            Date expiredTime = new Date(now.getTime() + 3600 * 1000);
            VideoAuditResponse response = cosFeign.cosVideoAudit(
                    new URI(auditUrl),
                    getCosAuthString(HttpMethodName.POST, "/video/auditing", expiredTime),
                    request);

            String jobId = response.getJobsDetail().getJobId();
            if (StringUtils.isEmpty(jobId)) {
                log.error("[async] video audit job id is empty");
                throw new RuntimeException("video audit job id is empty");
            }

            String queryUrl = String.format("%s/video/auditing/%s", host, jobId);

            while (true) {
                VideoAuditQueryResponse queryResponse = cosFeign.cosVideAuditQuery(
                        new URI(queryUrl),
                        getCosAuthString(HttpMethodName.GET, "/video/auditing/" + jobId, expiredTime));

                if (queryResponse.getJobsDetail().getState().equals("Failed")) {
                    log.error("[async] video audit query failed: {} ", queryResponse.getJobsDetail().getMessage());
                    throw new RuntimeException("video audit query failed");
                }

                if (queryResponse.getJobsDetail().getState().equals("Success")) {
                    return queryResponse;
                }

                Thread.sleep(2000);
            }
        } catch (URISyntaxException e) {
            log.error("[async] create URI exception: {}", e.getMessage());
            throw new RuntimeException("video audit failed");
        } catch (InterruptedException e) {
            log.error("[async] exception: {}", e.getMessage());
            throw new RuntimeException("video audit failed");
        }
    }
}
