package com.fmisser.gtc.social.service.impl;

import com.fmisser.gtc.social.domain.Young;
import com.fmisser.gtc.social.repository.YoungRepository;
import com.fmisser.gtc.social.service.YoungService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class YoungServiceImpl implements YoungService {

    @Resource
    private YoungRepository youngRepository;

    @Override
    public Young create(String phone, String code, int gender) {
        // check exist
        if (youngRepository.existsByPhone(phone)) {
            // go to login
            return login(phone, code);
        }

//        Young young = new Young();
        return null;
    }

    @Override
    public Young login(String phone, String code) {
        return null;
    }
}
