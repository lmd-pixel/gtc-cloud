package com.fmisser.gtc.social.service.impl;

import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.social.domain.DeviceForbidden;
import com.fmisser.gtc.social.repository.DeviceForbiddenRepository;
import com.fmisser.gtc.social.service.DeviceForbiddenService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author by fmisser
 * @create 2021/6/23 2:34 下午
 * @description TODO
 */

@Service
@AllArgsConstructor
public class DeviceForbiddenServiceImpl implements DeviceForbiddenService {
    private final DeviceForbiddenRepository deviceForbiddenRepository;

    @Override
    public int forbidden(int type, String identity, int days, String message) throws ApiException {
        return 0;
    }

    @Override
    public int disableForbidden(String identity) throws ApiException {
        return 0;
    }

    @Override
    public DeviceForbidden getDeviceForbidden(String identity) throws ApiException {
        return null;
    }
}
