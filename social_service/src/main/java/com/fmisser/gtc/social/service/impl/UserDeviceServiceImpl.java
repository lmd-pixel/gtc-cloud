package com.fmisser.gtc.social.service.impl;

import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.social.domain.User;
import com.fmisser.gtc.social.domain.UserDevice;
import com.fmisser.gtc.social.repository.UserDeviceRepository;
import com.fmisser.gtc.social.service.UserDeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Objects;

/**
 * @author fmisser
 * @create 2021-04-20 下午4:08
 * @description
 */
@Service
public class UserDeviceServiceImpl implements UserDeviceService {

    @Autowired
    private UserDeviceRepository userDeviceRepository;

    @Override
    public int create(User user, int deviceType, String deviceName, String deviceCategory,
                      String idfa, String imei, String androidId, String oaid,
                      String deviceToken, Integer physicalDevice, String osVersion,
                      String channel, String deviceDescribe, String ipAddr, String lang,
                      String appVersion, String sysVersion, String deviceBrand, String deviceIfv,String localizedmodel
                      ) throws ApiException {
        UserDevice userDevice = new UserDevice();
        userDevice.setUserId(user.getId());
        userDevice.setDeviceType(deviceType);
        if (!StringUtils.isEmpty(deviceName)) {
            userDevice.setDeviceName(deviceName);
        }
        if (!StringUtils.isEmpty(deviceCategory)) {
            userDevice.setDeviceCategory(deviceCategory);
        }
        if (!StringUtils.isEmpty(idfa)) {
            userDevice.setDeviceIdfa(idfa);
        }
        if (!StringUtils.isEmpty(imei)) {
            userDevice.setDeviceImei(imei);
        }
        if (!StringUtils.isEmpty(androidId)) {
            userDevice.setDeviceAndroidId(androidId);
        }
        if (!StringUtils.isEmpty(oaid)) {
            userDevice.setDeviceOaid(oaid);
        }
        if (!StringUtils.isEmpty(deviceToken)) {
            userDevice.setDeviceToken(deviceToken);
        }
        if (Objects.nonNull(physicalDevice)) {
            userDevice.setPhysicalDevice(physicalDevice);
        }
        if (!StringUtils.isEmpty(osVersion)) {
            userDevice.setOsVersion(osVersion);
        }
        if (!StringUtils.isEmpty(channel)) {
            userDevice.setChannel(channel);
        }
        if (!StringUtils.isEmpty(deviceDescribe)) {
            userDevice.setDeviceDescribe(deviceDescribe);
        }
        if (!StringUtils.isEmpty(ipAddr)) {
            userDevice.setIpAddr(ipAddr);
        }
        if (!StringUtils.isEmpty(lang)) {
            userDevice.setLang(lang);
        }
        if (!StringUtils.isEmpty(appVersion)){
            userDevice.setAppVersion(appVersion);
        }

        if (!StringUtils.isEmpty(sysVersion)){
            userDevice.setSysVersion(sysVersion);
        }

        if (!StringUtils.isEmpty(deviceBrand)){
            userDevice.setDeviceBrand(deviceBrand);
        }

        if (!StringUtils.isEmpty(deviceIfv)){
            userDevice.setDeviceIfv(deviceIfv);
        }

        if (!StringUtils.isEmpty(localizedmodel)){
            userDevice.setLocalizedmodel(localizedmodel);
        }


        userDeviceRepository.save(userDevice);

        return 1;
    }

    @Override
    public UserDevice getRecentUserDevice(User user) throws ApiException {
        return userDeviceRepository.getTopByUserIdOrderByCreateTimeDesc(user.getId());
    }
}
