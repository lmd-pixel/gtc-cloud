package com.fmisser.gtc.social.service.impl;

import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.social.domain.User;
import com.fmisser.gtc.social.domain.UserDevice;
import com.fmisser.gtc.social.repository.UserDeviceRepository;
import com.fmisser.gtc.social.service.UserDeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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
    public int create(User user, int deviceType, String deviceName, String deviceCategory, String deviceIdfa, String deviceToken, String ipAddr) throws ApiException {
        UserDevice userDevice = new UserDevice();
        userDevice.setUserId(user.getId());
        userDevice.setDeviceType(deviceType);
        if (!StringUtils.isEmpty(deviceName)) {
            userDevice.setDeviceName(deviceName);
        }
        if (!StringUtils.isEmpty(deviceCategory)) {
            userDevice.setDeviceCategory(deviceCategory);
        }
        if (!StringUtils.isEmpty(deviceIdfa)) {
            userDevice.setDeviceIdfa(deviceIdfa);
        }
        if (!StringUtils.isEmpty(deviceToken)) {
            userDevice.setDeviceToken(deviceToken);
        }
        if (!StringUtils.isEmpty(ipAddr)) {
            userDevice.setIpAddr(ipAddr);
        }

        userDeviceRepository.save(userDevice);

        return 1;
    }
}
