package com.fmisser.gtc.social.controller;

import com.fmisser.gtc.base.response.ApiResp;
import com.fmisser.gtc.social.domain.User;
import com.fmisser.gtc.social.service.ReportService;
import com.fmisser.gtc.social.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(description = "举报API")
@RestController
@RequestMapping("/report")
@Validated
public class ReportController {

    private final ReportService reportService;

    private final UserService userService;

    public ReportController(ReportService reportService,
                            UserService userService) {
        this.reportService = reportService;
        this.userService = userService;
    }

    @ApiOperation("举报用户")
    @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "String", paramType = "header")
    @PostMapping(value = "/pick-user")
    public ApiResp<Integer> reportUser(@RequestParam("dstUserDigitId") String dstUserDigitId,
                                     @RequestParam("message") String message) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getPrincipal().toString();
        User userDo = userService.getUserByUsername(username);

        User dstUserDo = userService.getUserByDigitId(dstUserDigitId);

        int ret = reportService.reportUser(userDo, dstUserDo, message);
        return ApiResp.succeed(ret);
    }

    @ApiOperation("举报用户动态")
    @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "String", paramType = "header")
    @PostMapping(value = "/pick-dynamic")
    public ApiResp<Integer> reportDynamic(@RequestParam("dstDynamaicId") Long dstDynamaicId,
                                     @RequestParam("message") String message) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getPrincipal().toString();
        User userDo = userService.getUserByUsername(username);

        int ret = reportService.reportDynamic(userDo, dstDynamaicId, message);
        return ApiResp.succeed(ret);
    }

}
