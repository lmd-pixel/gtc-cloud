package com.fmisser.gtc.social.service;

import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.social.domain.Moderation;

import java.util.List;

public interface ModerationService {
    // 通用
    List<Moderation> getModerationList() throws ApiException;

    // 动态 1： 文本 2： 照片 3： 视频
    List<Moderation> getDynamicModerationList(int type) throws ApiException;
}
