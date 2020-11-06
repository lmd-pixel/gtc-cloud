package com.fmisser.gtc.notice.feign;

import com.fmisser.gtc.base.dto.social.FollowDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "social")
@Service
public interface SocialService {
    @GetMapping(value = "/follow/list")
    public List<FollowDto> getFollowers(@RequestParam("youngId") Long youngId);
}
