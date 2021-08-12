package com.fmisser.gtc.social.service;

import com.fmisser.gtc.base.dto.social.ForbiddenDto;
import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.social.domain.DeviceForbidden;
import com.fmisser.gtc.social.domain.Forbidden;
import com.fmisser.gtc.social.domain.User;
import com.fmisser.gtc.social.domain.UserDevice;
import org.springframework.data.util.Pair;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface ForbiddenService {
    /**
     * 封号，如果当前时间内已经被封号或者存在永久封号，会失败
     * @param days 0 表示永久封号, -1 表示注销账号
     */
    int forbidden(User user, int days, String message) throws ApiException;


    int deviceceForbidden(UserDevice userDevice, String type,int days, String message) throws ApiException;

    /**
     * 取消封号 取消所有已经封号的记录
     */
    int disableForbidden(User user) throws ApiException;

    /**
     * 获取用户当前封号，如果没有，返回null
     */
    Forbidden getUserForbidden(User user) throws ApiException;

    /**
     * 获取当前封号列表
     */
    Pair<List<ForbiddenDto>, Map<String, Object>> getForbiddenList(String digitId, String nick, Integer identity,
                                                                   Integer pageSize, Integer pageIndex) throws ApiException;

    /**
     * 根据forbidden id 封号
     */
    int disableForbidden(Long forbiddenId) throws ApiException;
}
