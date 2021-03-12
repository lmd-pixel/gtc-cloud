package com.fmisser.gtc.social.controller;

import com.fmisser.gtc.base.response.ApiResp;
import com.fmisser.gtc.social.domain.Banner;
import com.fmisser.gtc.social.service.BannerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(description = "广告API")
@RestController
@RequestMapping("/banner")
@Validated
public class BannerController {

    @Autowired
    private BannerService bannerService;

    @ApiOperation("广告列表")
    @GetMapping("/list")
    ApiResp<List<Banner>> getBannerList(@RequestParam("lang") String lang) {
        List<Banner> bannerList = bannerService.getBannerList(lang);
        return ApiResp.succeed(bannerList);
    }
}
