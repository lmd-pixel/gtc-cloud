package com.fmisser.fpp.oss.cos.service;

import com.fmisser.fpp.oss.abs.service.OssService;
import com.fmisser.fpp.oss.cos.dto.RecognitionResult;
import com.fmisser.fpp.oss.cos.dto.VideoAuditQueryResponse;
import com.fmisser.fpp.oss.cos.dto.VideoAuditResponse;
import com.qcloud.cos.http.HttpMethodName;

import java.util.Date;

/**
 * @author by fmisser
 * @create 2021/5/28 8:19 下午
 * @description TODO
 */
public interface CosService extends OssService {
    // cos的域名
    String getDomainName(String cdn, String bucketName) throws RuntimeException;

    String getPictureAuditDomainName(String bucketName) throws RuntimeException;

    String getVideoAuditDomainName(String bucketName) throws RuntimeException;

    String getCosAuthString(HttpMethodName methodName, String resourcePath, Date expiredTime) throws RuntimeException;

    RecognitionResult recognizePicture(String host, String picturePath) throws RuntimeException;

    VideoAuditQueryResponse recognizeVideo(String host, String videoPath) throws RuntimeException;
}

