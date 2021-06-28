package com.fmisser.gtc.social.service;

import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.social.domain.DeviceForbidden;

/**
 * @author by fmisser
 * @create 2021/6/23 2:26 下午
 * @description TODO
 */
public interface DeviceForbiddenService {
    int forbidden(int type, String identity, int days, String message) throws ApiException;
    int disableForbidden(String identity) throws ApiException;
    DeviceForbidden getDeviceForbidden(String identity) throws ApiException;
}
