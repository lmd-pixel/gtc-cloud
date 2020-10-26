package com.fmisser.gtc.social.controller;

import com.fmisser.gtc.social.domain.Young;
import com.fmisser.gtc.social.service.YoungService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Api("用户相关接口")
@RestController
@RequestMapping("/young")
@Validated
public class YoungController {

    @Resource
    private YoungService youngService;

    @ApiOperation(value = "创建用户信息")
    @ApiImplicitParam(name = "Authorization", required = true, dataType = "String", paramType = "header")
    @PostMapping(value = "create")
    Young create(@RequestParam("phone") String phone,
                 @RequestParam("code") String code,
                 @RequestParam("gender") int gender) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return youngService.create(phone, code, gender);
    }
}
