package com.fmisser.gtc.social.controller;

import com.fmisser.gtc.base.response.ApiResp;
import com.fmisser.gtc.social.service.SpreadService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.hibernate.validator.constraints.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@Api(description = "推广API")
@RestController
@RequestMapping("/spread")
@Validated
public class SpreadController {

    @Autowired
    private SpreadService spreadService;

    @ApiOperation("添加设备信息")
    @PostMapping("/add-device")
    ApiResp<Integer> addDevice(@RequestParam("type") @Range(min = 0, max = 1) int type,
                               @RequestParam(value = "idfa", required = false) String idfa) {

        int ret = spreadService.addSpread(type, idfa);
        return ApiResp.succeed(ret);
    }
}
