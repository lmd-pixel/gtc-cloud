package com.fmisser.gtc.social.service;

import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.social.domain.User;

public interface ConsumeService {
    int buyVip(User user) throws ApiException;
}
