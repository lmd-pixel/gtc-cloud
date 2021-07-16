package com.fmisser.fpp.oss.cos.conf;

import com.fasterxml.jackson.databind.DeserializationFeature;
//import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fmisser.fpp.oss.cos.utils.COSSigner;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.http.HttpProtocol;
import com.qcloud.cos.region.Region;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author by fmisser
 * @create 2021/5/28 8:27 下午
 * @description TODO
 */
@Configuration
public class CosConfig {

    @Bean
    COSCredentials credentials() {
        return new BasicCOSCredentials(CosDefine.COS_SECRET_ID, CosDefine.COS_SECRET_KEY);
    }

    @Bean
    COSClient cosClient(COSCredentials cosCredentials) {
        Region region = new Region(CosDefine.COS_REGION);
        ClientConfig clientConfig = new ClientConfig(region);
        clientConfig.setHttpProtocol(HttpProtocol.https);
        return new COSClient(cosCredentials, clientConfig);
    }

    @Bean
    COSSigner cosSigner() {
        return new COSSigner();
    }

//    @Bean
//    XmlMapper xmlMapper() {
//        XmlMapper xmlMapper = new XmlMapper();
//        xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//        return xmlMapper;
//    }
}
