package com.fmisser.gtc.social.service;

import com.fmisser.gtc.base.dto.social.FollowDto;
import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.base.response.ApiResp;

import java.util.List;

public interface FollowService {

    // 获取 userid 关注的人
    ApiResp<List<FollowDto>> getFollowsFrom(Long userId) throws ApiException;

    // 获取关注 userid 的人
    ApiResp<List<FollowDto>> getFollowsTo(Long userId) throws ApiException;

    // 关注或者取消关注某人
    ApiResp<FollowDto> follow(Long userIdFrom, Long userIdTo, boolean bFollow) throws ApiException;
}
