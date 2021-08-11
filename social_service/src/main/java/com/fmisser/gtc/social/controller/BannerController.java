package com.fmisser.gtc.social.controller;

import com.fmisser.gtc.base.response.ApiResp;
import com.fmisser.gtc.social.domain.Banner;
import com.fmisser.gtc.social.service.BannerService;
import com.fmisser.gtc.social.service.SysAppConfigService;
import com.fmisser.gtc.social.service.SysConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(description = "广告API")
@RestController
@RequestMapping("/banner")
@Validated
public class BannerController {

    @Autowired
    private BannerService bannerService;

    @Autowired
    private SysConfigService sysConfigService;

    @Autowired
    private SysAppConfigService sysAppConfigService;

    @ApiOperation("广告列表")
    @GetMapping("/list")
    ApiResp<List<Banner>> getBannerList(
            @RequestHeader(value = "version", required = false, defaultValue = "v1") String version,
            @RequestParam("lang") String lang) {

        // 针对版本审核
        if (sysAppConfigService.getAppAuditVersion(version).equals(version) && sysAppConfigService.getAppAuditVersionTime(version)) {
            List<Banner> bannerList = bannerService.getAuditBannerList(lang);
            return ApiResp.succeed(bannerList);
        } else {
            List<Banner> bannerList = bannerService.getBannerList(lang);
            return ApiResp.succeed(bannerList);
        }
    }
}
