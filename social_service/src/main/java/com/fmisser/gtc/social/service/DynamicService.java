package com.fmisser.gtc.social.service;

import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.social.domain.Dynamic;
import com.fmisser.gtc.social.domain.Interact;
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
    List<Dynamic> getUserDynamicList(long userId, int page, int pageSize) throws ApiException;
    // 评论或者点赞动态
    Interact newInteract(long dynamicId, long userId, int type, String content) throws ApiException;
    // 取消点赞或者评论
    Interact cancelInteract(long interactId, long userId) throws ApiException;
    // 获取评论数
    List<Interact> getCommentList(long dynamicId, int page, int pageSize) throws ApiException;
}
