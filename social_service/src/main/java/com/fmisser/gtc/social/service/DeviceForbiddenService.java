package com.fmisser.gtc.social.service;

import com.fmisser.gtc.base.dto.social.DeviceForbiddenDto;
import com.fmisser.gtc.base.dto.social.ForbiddenDto;
import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.social.domain.DeviceForbidden;
import com.fmisser.gtc.social.domain.User;
import org.springframework.data.util.Pair;

import java.util.List;
import java.util.Map;

/**
 * @author by fmisser
 * @create 2021/6/23 2:26 下午
 * @description TODO
 */
public interface DeviceForbiddenService {
    int forbidden(int type, String identity, int days, String message) throws ApiException;
    int disableForbidden(String identity) throws ApiException;
    DeviceForbidden getDeviceForbidden(String identity) throws ApiException;


    List<DeviceForbidden> findByUserId(Long userId) throws ApiException;

    /**
     * 根据forbidden id 封号
     */
    int disableForbidden(Long forbiddenId) throws ApiException;


    /**
     * 获取当前封号列表
     */
    Pair<List<DeviceForbiddenDto>, Map<String, Object>> getDeviceForbiddenList(String digitId, String nick, Integer identity,String deviceName,String ipAddress,
                                                                         Integer pageSize, Integer pageIndex) throws ApiException;


}
