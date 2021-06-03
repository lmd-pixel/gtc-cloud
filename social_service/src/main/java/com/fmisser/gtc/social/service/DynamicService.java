package com.fmisser.gtc.social.service;

import com.fmisser.gtc.base.dto.social.DynamicCommentDto;
import com.fmisser.gtc.base.dto.social.DynamicDto;
import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.social.domain.Dynamic;
import com.fmisser.gtc.social.domain.DynamicComment;
import com.fmisser.gtc.social.domain.DynamicHeart;
import com.fmisser.gtc.social.domain.User;
import com.tencentcloudapi.ssa.v20180608.models.DataEvent;
import org.springframework.data.util.Pair;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 动态的相关服务，包括动态的评论点赞等
 */

public interface DynamicService {
    // 新增动态
    DynamicDto create(User user, int type, String content, String city,
                      BigDecimal longitude, BigDecimal latitude,
                      Map<String, MultipartFile> multipartFileMap) throws ApiException;
    // 获取用户发布的动态列表
    List<DynamicDto> getUserDynamicList(User user, User selfUser, int pageIndex, int pageSize, String version) throws ApiException;
    // 点赞或者取消点赞
    int dynamicHeart(Long dynamicId, User selfUser, int isCancel) throws ApiException;
    // 添加评论
    int newDynamicComment(Long dynamicId, Long commentIdTo, User selfUser, User toUser, String content) throws ApiException;
    // 删除评论
    int delDynamicComment(Long commentId, User selfUser) throws ApiException;
    // 获取评论数据
    List<DynamicCommentDto> getDynamicCommentList(Long dynamicId, User selfUser, int pageIndex, int pageSize) throws ApiException;
    // 获取最新动态
    List<DynamicDto> getLatestDynamicList(User selfUser, int pageIndex, int pageSize, String version) throws ApiException;
    // 获取关注的人的最新动态
    List<DynamicDto> getFollowLatestDynamicList(User selfUser, int pageIndex, int pageSize, String version) throws ApiException;
    // 删除动态
    int delete(User user, Long dynamicId) throws ApiException;

    ///// 管理接口
    Pair<List<DynamicDto>, Map<String, Object>> managerListDynamic(String digitId, String nick, String content,
                                                                   Date startTime, Date endTime,
                                                                   int pageIndex, int pageSize) throws ApiException;

    Pair<List<DynamicDto>, Map<String, Object>> managerGuardListDynamic(String digitId, String nick, String content,
                                                                   Date startTime, Date endTime,
                                                                   int pageIndex, int pageSize) throws ApiException;
    int deleteDynamic(Long dynamicId) throws ApiException;

    int auditDynamic(Long dynamicId, int pass, String message) throws ApiException;

    // 兼容旧的
    List<DynamicDto> compat(List<DynamicDto> dynamicDtoList, String version) throws ApiException;
}
