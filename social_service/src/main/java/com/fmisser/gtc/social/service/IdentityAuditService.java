package com.fmisser.gtc.social.service;

import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.social.domain.IdentityAudit;
import com.fmisser.gtc.social.domain.User;

import java.util.Optional;

/**
 * 用户身份审核
 */

public interface IdentityAuditService {
    // 获取最近的用户资料的审核，可能为空，表示未进行过审核
    Optional<IdentityAudit> getLastProfileAudit(User user) throws ApiException;

    // 获取最近的照片资料的审核，可能为空，表示未进行过审核
    Optional<IdentityAudit> getLastPhotosAudit(User user) throws ApiException;

    // 请求身份认证审核
    int requestIdentityAudit(User user) throws ApiException;

    // 响应身份认证审核结果
    // 由满足权限的审核角色操作
    int responseAudit(Long auditId, int pass, String message) throws ApiException;
}
