package com.fmisser.gtc.social.service.impl;

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
    public List<Long> getFollows(Long youngId) {
        return followRepository.getFollows(youngId);
    }
}
