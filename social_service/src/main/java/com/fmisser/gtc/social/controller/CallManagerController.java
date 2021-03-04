package com.fmisser.gtc.social.controller;

import com.fmisser.gtc.base.dto.social.CallDetailDto;
import com.fmisser.gtc.base.dto.social.CallDto;
import com.fmisser.gtc.base.response.ApiResp;
import com.fmisser.gtc.social.domain.User;
import com.fmisser.gtc.social.service.CallManagerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import org.hibernate.validator.constraints.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Api(description = "通话管理")
@RestController
@RequestMapping("/call-manager")
@Validated
public class CallManagerController {
    @Autowired
    private CallManagerService callManagerService;

    @GetMapping("/list")
    @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "String", paramType = "header")
    @PreAuthorize("hasAnyRole('MANAGER')")
    ApiResp<List<CallDto>> getCallList(@RequestParam(value = "callDigitId", required = false) String callDigitId,
                                       @RequestParam(value = "callNick", required = false) String callNick,
                                       @RequestParam(value = "acceptDigitId", required = false) String acceptDigitId,
                                       @RequestParam(value = "acceptNick", required = false) String acceptNick,
                                       @RequestParam(value = "type", required = false) @Range(min = 0, max = 1) Integer type,
                                       @RequestParam(value = "connected", required = false) @Range(min = 0, max = 1) Integer connected,
                                       @RequestParam(value = "startTime", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date startTime,
                                       @RequestParam(value = "endTime", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date endTime,
                                       @RequestParam(value = "pageIndex", required = false, defaultValue = "1") int pageIndex,
                                       @RequestParam(value = "pageSize", required = false, defaultValue = "30") int pageSize) {

        Pair<List<CallDto>, Map<String, Object>> callDtoList = callManagerService.getCallList(
                callDigitId, callNick, acceptDigitId, acceptNick, type, connected,
                startTime, endTime, pageIndex, pageSize);

        return ApiResp.succeed(callDtoList.getFirst(), callDtoList.getSecond());
    }

    @GetMapping("/detail")
    @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "String", paramType = "header")
    @PreAuthorize("hasAnyRole('MANAGER')")
    ApiResp<CallDetailDto> getCallDetail(@RequestParam(value = "callId") Long callId) {
        CallDetailDto callDetailDto = callManagerService.getCallDetail(callId);
        return ApiResp.succeed(callDetailDto);
    }

}
