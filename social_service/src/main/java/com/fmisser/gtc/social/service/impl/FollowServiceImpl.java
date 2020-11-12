package com.fmisser.gtc.social.service.impl;

import com.fmisser.gtc.base.dto.social.FollowDto;
import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.base.response.ApiResp;
import com.fmisser.gtc.social.domain.Follow;
import com.fmisser.gtc.social.repository.FollowRepository;
import com.fmisser.gtc.social.service.FollowService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FollowServiceImpl implements FollowService {

    private final FollowRepository followRepository;

    public FollowServiceImpl(FollowRepository followRepository) {
        this.followRepository = followRepository;
    }

    @Override
    public ApiResp<List<FollowDto>> getFollowsFrom(Long userId) throws ApiException {
        return ApiResp.succeed(followRepository.getFollowsFrom(userId));
    }

    @Override
    public ApiResp<List<FollowDto>> getFollowsTo(Long userId) throws ApiException {
        return ApiResp.succeed(followRepository.getFollowsTo(userId));
    }

    @Override
    public ApiResp<FollowDto> follow(Long userIdFrom, Long userIdTo, boolean bFollow) throws ApiException {
        Follow follow = followRepository.findByUserIdFromAndUserIdTo(userIdFrom, userIdTo);
        if (follow == null) {
            follow = new Follow();
            follow.setUserIdFrom(userIdFrom);
            follow.setUserIdTo(userIdTo);
        }

        if (bFollow) {
            follow.setStatus(1);
        } else {
            follow.setStatus(0);
        }

        follow = followRepository.save(follow);

        // create dto
        FollowDto followDto = new FollowDto();
        followDto.setUserIdFrom(follow.getUserIdFrom());
        followDto.setUserIdTo(follow.getUserIdTo());
        followDto.setCreateTime(follow.getCreateTime());

        return ApiResp.succeed(followDto);
    }
}
