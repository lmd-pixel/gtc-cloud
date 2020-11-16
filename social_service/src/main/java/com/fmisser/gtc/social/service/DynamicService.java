package com.fmisser.gtc.social.service;

import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.social.domain.Dynamic;
import com.fmisser.gtc.social.domain.DynamicComment;
import com.fmisser.gtc.social.domain.DynamicHeart;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * 动态的相关服务，包括动态的评论点赞等
 */

public interface DynamicService {
    // 新增动态
    Dynamic create(long userId, int type, String content, Map<String, MultipartFile> multipartFileMap) throws ApiException;
    // 获取用户发布的动态列表
    List<Dynamic> getUserDynamicList(long userId, long selfUserId, int pageIndex, int pageSize) throws ApiException;
    // 点赞或者取消点赞
    int dynamicHeart(long dynamicId, long userId, String nickname, int isCancel) throws ApiException;
    // 添加评论
    int newDynamicComment(long dynamicId, long userId, String nickname, String content) throws ApiException;
    // 删除评论
    int delDynamicComment(long commentId, long userId) throws ApiException;
    // 获取评论数据
    List<DynamicComment> getDynamicCommentList(long dynamicId, int page, int pageSize) throws ApiException;
}
