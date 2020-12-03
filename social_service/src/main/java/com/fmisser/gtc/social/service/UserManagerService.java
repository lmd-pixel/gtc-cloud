package com.fmisser.gtc.social.service;

import com.fmisser.gtc.base.dto.social.*;
import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.social.domain.IdentityAudit;
import com.fmisser.gtc.social.domain.User;

import java.util.Date;
import java.util.List;

/**
 * 用户管理服务
 */

public interface UserManagerService {

    // 获取主播列表
    List<AnchorDto> getAnchorList(String digitId, String nick, String phone, Integer gender,
                                  Date startTime, Date endTime,
                                  int pageIndex, int pageSize,
                                  int sortColumn, int sortDirection) throws ApiException;

    // 获取普通用户列表
    List<ConsumerDto> getConsumerList(String digitId, String nick, String phone,
                                      Date startTime, Date endTime,
                                      int pageIndex, int pageSize,
                                      int sortColumn, int sortDirection) throws ApiException;

    // 获取用户详情
    User getUserProfile(String digitId) throws ApiException;

    // 获取推荐主播列表
    List<RecommendDto> getRecommendList(String digitId, String nick, Integer type,
                                        int pageIndex, int pageSize) throws ApiException;

    // 设置主播推荐
    int configRecommend(String digitId, int type, int recommend, Long level) throws ApiException;

    // 获取主播审核列表
    List<IdentityAudit> getAnchorAuditList(String digitId, String nick, Integer gender, Integer status,
                                           Date startTime, Date endTime,
                                           int pageIndex, int pageSize) throws ApiException;

    // 审核主播资料或者相册
    int anchorAudit(String serialNumber, int operate, String message) throws ApiException;
}
