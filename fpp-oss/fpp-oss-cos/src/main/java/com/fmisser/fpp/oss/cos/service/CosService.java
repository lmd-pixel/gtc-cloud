package com.fmisser.fpp.oss.cos.service;

import com.fmisser.fpp.oss.abs.service.OssService;

/**
 * @author by fmisser
 * @create 2021/5/28 8:19 下午
 * @description TODO
 */
public interface CosService extends OssService {
    // cos的域名
    String getDomainName(String bucketName) throws RuntimeException;
}

