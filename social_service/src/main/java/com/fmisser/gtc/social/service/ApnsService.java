package com.fmisser.gtc.social.service;

import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.social.domain.User;

public interface ApnsService {
    int pushToOneUser(User user) throws ApiException;

    int pushToAllUser() throws ApiException;

    int pushToAllAnchor() throws ApiException;
}
