package com.fmisser.gtc.social.service;

import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.social.domain.User;
import com.fmisser.gtc.social.domain.UserDevice;

/**
 * @author fmisser
 * @create 2021-04-20 下午4:07
 * @description
 */
public interface UserDeviceService {
    int create(User user, int deviceType, String deviceName, String deviceCategory,
               String idfa, String imei, String androidId, String oaid,
               String deviceToken, Integer physicalDevice, String osVersion,
               String channel, String deviceDescribe, String ipAddr, String lang) throws ApiException;
    UserDevice getRecentUserDevice(User user) throws ApiException;
}
