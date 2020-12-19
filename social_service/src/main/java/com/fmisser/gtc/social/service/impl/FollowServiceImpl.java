package com.fmisser.gtc.social.service.impl;

import com.fmisser.gtc.base.dto.social.FollowDto;
import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.base.response.ApiResp;
import com.fmisser.gtc.social.domain.Follow;
import com.fmisser.gtc.social.domain.User;
import com.fmisser.gtc.social.repository.FollowRepository;
import com.fmisser.gtc.social.repository.UserRepository;
import com.fmisser.gtc.social.service.FollowService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class FollowServiceImpl implements FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    public FollowServiceImpl(FollowRepository followRepository,
                             UserRepository userRepository) {
        this.followRepository = followRepository;
        this.userRepository = userRepository;
    }

    @Override
    public ApiResp<List<FollowDto>> getFollowsFrom(Long userId) throws ApiException {
        return ApiResp.succeed(followRepository.getFollowsFrom(userId));
    }

    @Override
    public ApiResp<List<FollowDto>> getFollowsTo(Long userId) throws ApiException {
        return ApiResp.succeed(followRepository.getFollowsTo(userId));
    }

    @Transactional
    @Override
    public ApiResp<FollowDto> follow(Long userIdFrom, Long userIdTo, boolean bFollow) throws ApiException {
        Follow follow = followRepository.findByUserIdFromAndUserIdTo(userIdFrom, userIdTo);
        if (follow == null) {
            follow = new Follow();
            follow.setUserIdFrom(userIdFrom);
            follow.setUserIdTo(userIdTo);
        }

//        Optional<User> userOptional = userRepository.findById(userIdTo);
//        if (!userOptional.isPresent()) {
//            throw new ApiException(-1, "被关注的用户不存在");
//        }
//        User userTo = userOptional.get();

        if (bFollow) {
            follow.setStatus(1);
            userRepository.addUserFollow(userIdTo);
        } else {
            follow.setStatus(0);
            userRepository.subUserFollow(userIdTo);
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
