package com.fmisser.gtc.social.service.impl;

import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.social.domain.User;
import com.fmisser.gtc.social.service.ApnsService;
import com.tencent.xinge.XingeApp;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * 腾讯推送
 * https://cloud.tencent.com/document/product/548/41759
 */

@Service
public class TencentApnsService implements ApnsService {
//    private static XingeApp xingeApp;
//
//    @PostConstruct
//    public void init() {
//        xingeApp = new XingeApp.Builder()
//                .appId("")
//                .secretKey("")
//                .domainUrl("https://api.tpns.tencent.com/")
//                .build();
//    }

    @Bean
    public XingeApp getXingeApp() {
        return new XingeApp.Builder()
                .appId("")
                .secretKey("")
                .domainUrl("https://api.tpns.tencent.com/")
                .build();
    }


    @Override
    public int pushToOneUser(User user) throws ApiException {
        return 0;
    }

    @Override
    public int pushToAllUser() throws ApiException {
        return 0;
    }

    @Override
    public int pushToAllAnchor() throws ApiException {
        return 0;
    }
}
