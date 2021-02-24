package com.fmisser.gtc.social.service;

import com.fmisser.gtc.base.dto.social.*;
import com.fmisser.gtc.base.dto.social.calc.CalcTotalProfitDto;
import com.fmisser.gtc.base.dto.social.calc.CalcUserDto;
import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.social.domain.IdentityAudit;
import com.fmisser.gtc.social.domain.User;
import org.springframework.data.util.Pair;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 用户管理服务
 */

public interface UserManagerService {

    // 获取主播列表
    Pair<List<AnchorDto>,Map<String, Object>> getAnchorList(String digitId, String nick, String phone, Integer gender,
                                  Date startTime, Date endTime,
                                  int pageIndex, int pageSize,
                                  int sortColumn, int sortDirection) throws ApiException;

    // 获取普通用户列表
    Pair<List<ConsumerDto>,Map<String, Object>> getConsumerList(String digitId, String nick, String phone,
                                      Date startTime, Date endTime,
                                      int pageIndex, int pageSize,
                                      int sortColumn, int sortDirection) throws ApiException;

    // 获取用户详情
    User getUserProfile(String digitId) throws ApiException;

    // 获取推荐主播列表
    Pair<List<RecommendDto>, Map<String, Object>> getRecommendList(String digitId, String nick, Integer gender, Integer type,
                                        int pageIndex, int pageSize) throws ApiException;

    // 设置主播推荐
    int configRecommend(String digitId, int type, int recommend, Long level, Date startTime, Date endTime) throws ApiException;

    // 获取主播审核列表
    Pair<List<IdentityAudit>, Map<String, Object>> getAnchorAuditList(String digitId, String nick,
                                                                      Integer gender, Integer status,
                                                                      Date startTime, Date endTime,
                                                                      int pageIndex, int pageSize) throws ApiException;

    List<IdentityAudit> getAnchorAudit(String digitId) throws ApiException;

    // 审核主播资料/相册/视频
    int anchorAudit(String serialNumber, int operate, String message) throws ApiException;

    // 获取统计用户数据
    Pair<List<CalcUserDto>, Map<String, Object>> getCalcUser(Date startTime, Date endTime,
                                                             int pageIndex, int pageSize) throws ApiException;

    // 获取统计收益数据
    Pair<List<CalcTotalProfitDto>, Map<String, Object>> getCalcTotalProfit(Date startTime, Date endTime,
                                                                           int pageIndex, int pageSize) throws ApiException;
}
