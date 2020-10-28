package com.fmisser.gtc.social.service;

import java.util.List;

public interface FollowService {
    List<Long> getFollows(Long youngId);
}
