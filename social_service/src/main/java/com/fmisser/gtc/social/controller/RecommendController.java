package com.fmisser.gtc.social.controller;

import com.fmisser.gtc.base.dto.social.RecommendAnchorDto;
import com.fmisser.gtc.base.dto.social.RecommendDto;
import com.fmisser.gtc.base.response.ApiResp;
import com.fmisser.gtc.social.domain.User;
import com.fmisser.gtc.social.service.RecommendService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(description = "推荐API")
@RestController
@RequestMapping("/recommend")
@Validated
public class RecommendController {

    private final RecommendService recommendService;

    public RecommendController(RecommendService recommendService) {
        this.recommendService = recommendService;
    }

    @ApiOperation(value = "获取随机推荐主播")
//    @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "String", paramType = "header")
    @GetMapping(value = "/rand-anchor-list")
    ApiResp<List<RecommendAnchorDto>> gerRandRecommendAnchorList(
            @RequestParam(value = "count", required = false, defaultValue = "6") Integer count ) {
        List<RecommendAnchorDto> recommendDtoList = recommendService.getRandRecommendAnchorList(count);
        return ApiResp.succeed(recommendDtoList);
    }
}
