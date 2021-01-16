package com.fmisser.gtc.social.service;

import com.fmisser.gtc.base.dto.social.ConcernDto;
import com.fmisser.gtc.base.dto.social.FansDto;
import com.fmisser.gtc.base.dto.social.FollowDto;
import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.base.response.ApiResp;
import com.fmisser.gtc.social.domain.User;

import java.util.List;

public interface FollowService {

    // 获取 userid 关注的人
    ApiResp<List<FollowDto>> getFollowsFrom(Long userId) throws ApiException;

    // 获取关注 userid 的人
    ApiResp<List<FollowDto>> getFollowsTo(Long userId) throws ApiException;

    // 关注或者取消关注某人
    ApiResp<FollowDto> follow(User userFrom, User userTo, boolean bFollow) throws ApiException;

    // 获取关注的人列表
    List<ConcernDto> getConcernList(User user, int pageIndex, int pageSize) throws ApiException;

    // 获取粉丝列表
    List<FansDto> getFansList(User user, int pageIndex, int pageSize) throws ApiException;
}
