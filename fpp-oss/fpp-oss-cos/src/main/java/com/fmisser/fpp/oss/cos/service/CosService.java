package com.fmisser.fpp.oss.cos.service;

import com.fmisser.fpp.oss.abs.service.OssService;
//import com.fmisser.fpp.oss.cos.dto.RecognitionResult;
//import com.fmisser.fpp.oss.cos.dto.VideoAuditQueryResponse;
//import com.fmisser.fpp.oss.cos.dto.VideoAuditResponse;
import com.qcloud.cos.http.HttpMethodName;
import com.qcloud.cos.model.AccessControlList;

import java.io.InputStream;
import java.util.Date;
import java.util.Map;

/**
 * @author by fmisser
 * @create 2021/5/28 8:19 下午
 * @description TODO
 */
public interface CosService extends OssService {

    String putObject(String bucketName, String objectName, Map<String, String> headers, InputStream inputStream, Long size, String contentType) throws RuntimeException;
    // cos的域名
    String getDomainName(String cdn, String bucketName) throws RuntimeException;

    String getPictureAuditDomainName(String bucketName) throws RuntimeException;

    String getVideoAuditDomainName(String bucketName) throws RuntimeException;

    String getCosAuthString(HttpMethodName methodName, String resourcePath, Date expiredTime) throws RuntimeException;

    AccessControlList getObjectAcl(String bucketName, String objectName) throws RuntimeException;
    Integer setObjectAcl(String bucketName, String objectName, AccessControlList acl) throws RuntimeException;

//    RecognitionResult recognizePicture(String host, String picturePath) throws RuntimeException;
//
//    VideoAuditQueryResponse recognizeVideo(String host, String videoPath) throws RuntimeException;
}

