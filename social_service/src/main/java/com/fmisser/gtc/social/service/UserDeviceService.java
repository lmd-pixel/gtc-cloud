package com.fmisser.gtc.social.service;

import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.social.domain.User;

/**
 * @author fmisser
 * @create 2021-04-20 下午4:07
 * @description
 */
public interface UserDeviceService {
    int create(User user, int deviceType, String deviceName, String deviceCategory, String idfa, String deviceToken, String ipAddr) throws ApiException;
}
