package com.fmisser.gtc.notice.controller;

import com.fmisser.gtc.notice.feign.SocialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/notice")
public class NoticeController {

    private final SocialService socialService;

    public NoticeController(SocialService socialService) {
        this.socialService = socialService;
    }

    @GetMapping(value = "/list")
    List<Long> getNoticeList(@RequestParam("youngId") Long youngId) {
        return socialService.getFollowers(youngId);
    }
}
