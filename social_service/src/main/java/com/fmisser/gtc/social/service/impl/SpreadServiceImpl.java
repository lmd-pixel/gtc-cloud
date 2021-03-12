package com.fmisser.gtc.social.service.impl;

import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.social.repository.SpreadRepository;
import com.fmisser.gtc.social.service.SpreadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SpreadServiceImpl implements SpreadService {

    @Autowired
    private SpreadRepository spreadRepository;

    @Override
    public int addSpread(int type, String idfa) throws ApiException {
        return 0;
    }
}
