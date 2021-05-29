package com.fmisser.gtc.social.service;

import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.social.domain.User;
import com.fmisser.gtc.social.domain.UserMaterial;

import java.util.Collection;
import java.util.List;

/**
 * @author by fmisser
 * @create 2021/5/26 4:40 下午
 * @description TODO
 */
public interface UserMaterialService {
    // 获取预审核照片列表
    List<UserMaterial> getAuditPreparePhotos(User user) throws ApiException;

    // 获取审核列表
    List<UserMaterial> getAuditPhotos(User user) throws ApiException;

    // 获取当前用户照片
    List<UserMaterial> getPhotos(User user) throws ApiException;

    // 获取认证视频
    UserMaterial getAuditVideo(User user) throws ApiException;

    void deleteList(Collection<UserMaterial> userMaterialList) throws ApiException;

    void addList(Collection<UserMaterial> userMaterialList) throws ApiException;

    void updateList(Collection<UserMaterial> userMaterialList) throws ApiException;
}
