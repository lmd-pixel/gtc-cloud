package com.fmisser.gtc.social.controller;


import com.fmisser.gtc.base.dto.social.DeviceForbiddenDto;
import com.fmisser.gtc.base.dto.social.ForbiddenDto;
import com.fmisser.gtc.base.response.ApiResp;
import com.fmisser.gtc.social.domain.User;
import com.fmisser.gtc.social.domain.UserDevice;
import com.fmisser.gtc.social.service.DeviceForbiddenService;
import com.fmisser.gtc.social.service.ForbiddenService;
import com.fmisser.gtc.social.service.UserDeviceService;
import com.fmisser.gtc.social.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Api(description = "封号API")
@RestController
@RequestMapping("/forbidden-manager")
@Validated
public class ForbiddenManagerController {

    @Autowired
    private ForbiddenService forbiddenService;
    @Autowired
    private UserDeviceService userDeviceService;
    @Autowired
    private DeviceForbiddenService deviceForbiddenService;

    @Autowired
    private UserService userService;

    @ApiOperation(value = "获取封号列表")
    @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "String", paramType = "header")
    @GetMapping("/list")
    @PreAuthorize("hasAnyRole('MANAGER')")
    ApiResp<List<ForbiddenDto>> getForbiddenList(
            @RequestParam(value = "digitId", required = false) String digitId,
            @RequestParam(value = "nick", required = false) String nick,
            @RequestParam(value = "identity", required = false) Integer identity,
            @RequestParam(value = "pageIndex", required = false, defaultValue = "1") int pageIndex,
            @RequestParam(value = "pageSize", required = false, defaultValue = "30") int pageSize) {
        Pair<List<ForbiddenDto>, Map<String, Object>> forbiddenDtoList
                = forbiddenService.getForbiddenList(digitId, nick, identity, pageSize, pageIndex);
        return ApiResp.succeed(forbiddenDtoList.getFirst(), forbiddenDtoList.getSecond());
    }

    @ApiOperation(value = "封号")
    @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "String", paramType = "header")
    @PostMapping("/put")
    @PreAuthorize("hasAnyRole('MANAGER')")
    ApiResp<Integer> forbidden(@RequestParam(value = "digitId") String digitId,
                               @RequestParam(value = "days") Integer days,
                               @RequestParam(value = "message", required = false) String message) {
        User user = userService.getUserByDigitId(digitId);
        int ret = forbiddenService.forbidden(user, days, message);
        return ApiResp.succeed(ret);
    }

    @ApiOperation(value = "解封")
    @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "String", paramType = "header")
    @PostMapping("/cancel")
    @PreAuthorize("hasAnyRole('MANAGER')")
    ApiResp<Integer> cancelForbidden(@RequestParam(value = "forbiddenId") Long forbiddenId) {
        int ret = forbiddenService.disableForbidden(forbiddenId);
        return ApiResp.succeed(ret);
    }

    /**************
     * 封号（封锁设备或者ip）
     * @param deviceId
     * @param days
     * @param type
     * @param message
     * @return
     */
    @ApiOperation(value = "封号{封锁设备或ip}")
    @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "String", paramType = "header")
    @PostMapping("/deviceForbiden")
    @PreAuthorize("hasAnyRole('MANAGER')")
    ApiResp<Integer> deviceceForbidden(@RequestParam(value = "deviceId") long deviceId,
                                       @RequestParam(value = "days") Integer days,
                                       @RequestParam(value = "type") String type,
                                       @RequestParam(value = "message", required = false) String message) {
        UserDevice userDevice= userDeviceService.getUserDeviceById(deviceId);
        int ret = forbiddenService.deviceceForbidden(userDevice,type, days, message);
        return ApiResp.succeed(ret);
    }


    @ApiOperation(value = "获取封号列表")
    @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "String", paramType = "header")
    @GetMapping("/list-forbidden")
    @PreAuthorize("hasAnyRole('MANAGER')")
    ApiResp<List<DeviceForbiddenDto>> getDeviceForbiddenList(
            @RequestParam(value = "digitId", required = false) String digitId,
            @RequestParam(value = "nick", required = false) String nick,
            @RequestParam(value = "identity", required = false) Integer identity,
            @RequestParam(value = "deviceName", required = false) String deviceName,
            @RequestParam(value = "ipAddress", required = false) String ipAddress,
            @RequestParam(value = "pageIndex", required = false, defaultValue = "1") int pageIndex,
            @RequestParam(value = "pageSize", required = false, defaultValue = "30") int pageSize) {
        Pair<List<DeviceForbiddenDto>, Map<String, Object>> forbiddenDtoList
                = deviceForbiddenService.getDeviceForbiddenList(digitId, nick, identity,deviceName,ipAddress,pageSize, pageIndex);
        return ApiResp.succeed(forbiddenDtoList.getFirst(), forbiddenDtoList.getSecond());
    }

    @ApiOperation(value = "解封")
    @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "String", paramType = "header")
    @PostMapping("/device_cancel")
    @PreAuthorize("hasAnyRole('MANAGER')")
    ApiResp<Integer> cancelDevice(@RequestParam(value = "forbiddenId") Long forbiddenId) {
        int ret = deviceForbiddenService.disableForbidden(forbiddenId);
        return ApiResp.succeed(ret);
    }
}
