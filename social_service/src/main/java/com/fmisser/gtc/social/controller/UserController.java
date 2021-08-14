package com.fmisser.gtc.social.controller;

import com.fmisser.gtc.base.dto.social.GuardDto;
import com.fmisser.gtc.base.dto.social.ProfitConsumeDetail;
import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.base.response.ApiResp;
import com.fmisser.gtc.social.domain.Asset;
import com.fmisser.gtc.social.domain.IdentityAudit;
import com.fmisser.gtc.social.domain.SysAppConfig;
import com.fmisser.gtc.social.domain.User;
import com.fmisser.gtc.social.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.hibernate.validator.constraints.Range;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;

@Api(description = "用户API")
@RestController
@RequestMapping("/user")
@Validated
@AllArgsConstructor
public class UserController {

    private final UserService userService;
    private final AssetService assetService;
    private final IdentityAuditService identityAuditService;
    private final GreetService greetService;
    private final SysConfigService sysConfigService;
    private final UserDeviceService userDeviceService;
    private final AssistService assistService;
    private final GuardService guardService;
    private final SysAppConfigService sysAppConfigService;

    @ApiOperation(value = "创建用户")
    @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "String", paramType = "header")
    @PostMapping(value = "/create")
    ApiResp<User> create(@RequestHeader(value = "version", required = false, defaultValue = "v1") String version,
                         @RequestParam(value = "phone", required = false, defaultValue = "") String phone,
                         @RequestParam(value = "nick", required = false) String nick,
                         @RequestParam("gender") @Range(min = 0, max = 1) int gender,
                         @RequestParam(value = "invite", required = false) String invite,
                         @RequestParam(value = "channelId", required = false) String channelId,
                         @RequestParam(value = "ipAdress", required = false) String ipAdress,
                         @RequestParam(value = "deviceId", required = false) String deviceId
      ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getPrincipal().toString();
        if (!StringUtils.isEmpty(phone) &&
                !username.equals(phone)) {
            // return error
            throw new ApiException(-1, "非法操作，认证用户无法创建其他用户资料！");
        }
        User user = userService.create(username, gender, nick, invite, version,channelId,ipAdress,deviceId);

        // 针对版本审核
        if (sysConfigService.getAppAuditVersion().equals(version)) {
            user.setMessagePrice(BigDecimal.valueOf(-1).setScale(2, BigDecimal.ROUND_HALF_UP));
            user.setCallPrice(BigDecimal.valueOf(-1).setScale(2, BigDecimal.ROUND_HALF_UP));
            user.setVideoPrice(BigDecimal.valueOf(-1).setScale(2, BigDecimal.ROUND_HALF_UP));
        }

        return ApiResp.succeed(user);
    }

    @ApiOperation(value = "更新用户信息")
    @ApiImplicitParam(name = "Authorization", required = true, dataType = "String", paramType = "header")
    @PostMapping(value = "/update-profile")
    ApiResp<User> uploadProfile(MultipartHttpServletRequest request,
                                @RequestHeader(value = "version", required = false, defaultValue = "v1") String version,
                                @RequestParam(value = "update_type", required = false, defaultValue = "0") Integer updateType,
                                @RequestParam(value = "nick", required = false) String nick,
                                @RequestParam(value = "birth", required = false) String birth,
                                @RequestParam(value = "city", required = false) String city,
                                @RequestParam(value = "profession", required = false) String profession,
                                @RequestParam(value = "intro", required = false) String intro,
                                @RequestParam(value = "labels", required = false) String labels,
                                @RequestParam(value = "callPrice", required = false) String callPrice,
                                @RequestParam(value = "videoPrice", required = false) String videoPrice,
                                @RequestParam(value = "messagePrice", required = false) String messagePrice,
                                @RequestParam(value = "mode", required = false) Integer mode,
                                @RequestParam(value = "rest", required = false) Integer rest,
                                @RequestParam(value = "restStartDate", required = false) String restStartDate,
                                @RequestParam(value = "restEndDate", required = false) String restEndDate) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getPrincipal().toString();
        User userDo = userService.getUserByUsername(username);
        //
        // TODO: 2020/11/10 check params
        User user = userService.updateProfile(userDo, updateType,
                nick, birth, city, profession,
                intro, labels, callPrice, videoPrice, messagePrice,
                mode, rest, restStartDate, restEndDate,
                request.getFileMap());

        // 针对版本审核
        if (sysConfigService.getAppAuditVersion().equals(version)) {
            user.setMessagePrice(BigDecimal.valueOf(-1).setScale(2, BigDecimal.ROUND_HALF_UP));
            user.setCallPrice(BigDecimal.valueOf(-1).setScale(2, BigDecimal.ROUND_HALF_UP));
            user.setVideoPrice(BigDecimal.valueOf(-1).setScale(2, BigDecimal.ROUND_HALF_UP));
        }

        return ApiResp.succeed(user);
    }

    @ApiOperation(value = "更新用户照片")
    @ApiImplicitParam(name = "Authorization", required = true, dataType = "String", paramType = "header")
    @PostMapping(value = "/update-photos")
    ApiResp<User> uploadPhotos(MultipartHttpServletRequest request,
                               @RequestHeader(value = "version", required = false, defaultValue = "v1") String version,
                               @RequestParam(value = "update_type", required = false, defaultValue = "1") Integer updateType,
                               @RequestParam(value = "existPhotos", required = false) String existPhotos) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getPrincipal().toString();
        User userDo = userService.getUserByUsername(username);
        //
        // TODO: 2020/11/10 check params
        User user = userService.updatePhotos(userDo, updateType, existPhotos, request.getFileMap());

        // 针对版本审核
        if (sysConfigService.getAppAuditVersion().equals(version)) {
            user.setMessagePrice(BigDecimal.valueOf(-1).setScale(2, BigDecimal.ROUND_HALF_UP));
            user.setCallPrice(BigDecimal.valueOf(-1).setScale(2, BigDecimal.ROUND_HALF_UP));
            user.setVideoPrice(BigDecimal.valueOf(-1).setScale(2, BigDecimal.ROUND_HALF_UP));
        }

        return ApiResp.succeed(user);
    }

    @ApiOperation(value = "更新用户视频")
    @ApiImplicitParam(name = "Authorization", required = true, dataType = "String", paramType = "header")
    @PostMapping(value = "/update-video")
    ApiResp<User> uploadVideo(MultipartHttpServletRequest request,
                              @RequestHeader(value = "version", required = false, defaultValue = "v1") String version,
                              @RequestParam(value = "update_type", required = false, defaultValue = "1") Integer updateType) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getPrincipal().toString();
        User userDo = userService.getUserByUsername(username);
        //
        // TODO: 2020/11/10 check params
        User user = userService.updateVideo(userDo, updateType, request.getFileMap());

        // 针对版本审核
        if (sysConfigService.getAppAuditVersion().equals(version)) {
            user.setMessagePrice(BigDecimal.valueOf(-1).setScale(2, BigDecimal.ROUND_HALF_UP));
            user.setCallPrice(BigDecimal.valueOf(-1).setScale(2, BigDecimal.ROUND_HALF_UP));
            user.setVideoPrice(BigDecimal.valueOf(-1).setScale(2, BigDecimal.ROUND_HALF_UP));
        }

        return ApiResp.succeed(user);
    }

    @ApiOperation(value = "更新用户照片(守护版本)")
    @ApiImplicitParam(name = "Authorization", required = true, dataType = "String", paramType = "header")
    @PostMapping(value = "/update-photos-ex")
    ApiResp<User> uploadPhotosEx(MultipartHttpServletRequest request,
                                 @RequestHeader(value = "version", required = false, defaultValue = "v1") String version,
                                 @RequestParam(value = "update_type", required = false, defaultValue = "1") Integer updateType,
                                 @RequestParam(value = "existsNames", required = false) String existsNames,
                                 @RequestParam(value = "coverName", required = false) String coverName) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getPrincipal().toString();
        User userDo = userService.getUserByUsername(username);
        //
        // TODO: 2020/11/10 check params
        User user = userService.updatePhotosEx(userDo, updateType, existsNames, coverName, request.getFileMap());

        // 针对版本审核
        if (sysConfigService.getAppAuditVersion().equals(version)) {
            user.setMessagePrice(BigDecimal.valueOf(-1).setScale(2, BigDecimal.ROUND_HALF_UP));
            user.setCallPrice(BigDecimal.valueOf(-1).setScale(2, BigDecimal.ROUND_HALF_UP));
            user.setVideoPrice(BigDecimal.valueOf(-1).setScale(2, BigDecimal.ROUND_HALF_UP));
        }

        return ApiResp.succeed(user);
    }

    @ApiOperation(value = "更新守护照片(守护版本)")
    @ApiImplicitParam(name = "Authorization", required = true, dataType = "String", paramType = "header")
    @PostMapping(value = "/update-guard-photos")
    ApiResp<User> uploadGuardPhotos(MultipartHttpServletRequest request,
                                 @RequestHeader(value = "version", required = false, defaultValue = "v1") String version,
                                 @RequestParam(value = "update_type", required = false, defaultValue = "1") Integer updateType,
                                 @RequestParam(value = "existsGuards", required = false) String existsGuards) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getPrincipal().toString();
        User userDo = userService.getUserByUsername(username);
        //
        // TODO: 2020/11/10 check params
        User user = userService.updateGuardPhotos(userDo, updateType, existsGuards, request.getFileMap());

        // 针对版本审核
        if (sysConfigService.getAppAuditVersion().equals(version)) {
            user.setMessagePrice(BigDecimal.valueOf(-1).setScale(2, BigDecimal.ROUND_HALF_UP));
            user.setCallPrice(BigDecimal.valueOf(-1).setScale(2, BigDecimal.ROUND_HALF_UP));
            user.setVideoPrice(BigDecimal.valueOf(-1).setScale(2, BigDecimal.ROUND_HALF_UP));
        }

        return ApiResp.succeed(user);
    }

    @ApiOperation(value = "更新认证视频（守护版本）")
    @ApiImplicitParam(name = "Authorization", required = true, dataType = "String", paramType = "header")
    @PostMapping(value = "/update-audit-video")
    ApiResp<User> uploadAuditVideo(MultipartHttpServletRequest request,
                                   @RequestHeader(value = "version", required = false, defaultValue = "v1") String version,
                                   @RequestParam(value = "update_type", required = false, defaultValue = "1") Integer updateType,
                                   @RequestParam(value = "code", required = false, defaultValue = "1") Integer code) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getPrincipal().toString();
        User userDo = userService.getUserByUsername(username);
        //
        // TODO: 2020/11/10 check params
        User user = userService.updateAuditVideo(userDo, updateType, code, request.getFileMap());

        // 针对版本审核
        if (sysConfigService.getAppAuditVersion().equals(version)) {
            user.setMessagePrice(BigDecimal.valueOf(-1).setScale(2, BigDecimal.ROUND_HALF_UP));
            user.setCallPrice(BigDecimal.valueOf(-1).setScale(2, BigDecimal.ROUND_HALF_UP));
            user.setVideoPrice(BigDecimal.valueOf(-1).setScale(2, BigDecimal.ROUND_HALF_UP));
        }

        return ApiResp.succeed(user);
    }

    @ApiOperation(value = "获取用户信息")
    @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "String", paramType = "header")
    @GetMapping(value = "/profile")
    ApiResp<User> getProfile(
            @RequestHeader(value = "version", required = false, defaultValue = "v1") String version) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getPrincipal().toString();
        User userDo = userService.getUserByUsername(username);

        // 用户获取信息认为用户活跃，更新打招呼信息
        if(sysAppConfigService.getAppAuditVersion(version).equals(version)){
            SysAppConfig sysAppConfig=sysAppConfigService.getSysAppconfig(version);
            if(sysAppConfig!=null && sysAppConfig.getHarassIsStart().equals("1")){
                greetService.createGreet(userDo);
            }
        }else{
            if (sysConfigService.isMsgGreetEnable()) {
                greetService.createGreet(userDo);
            }
        }
//        if (sysConfigService.isMsgGreetEnable()) {
//            greetService.createGreet(userDo);
//        }

        // 当不是审核版本时发送充值消息
        if (!sysConfigService.getAppAuditVersion().equals(version)) {
            assistService.sendRechargeMsg(userDo);
        }

        User user = userService.getSelfProfile(userDo);

        //判断是否在审核中
        if (sysAppConfigService.getAppAuditVersion(version).equals(version) && sysAppConfigService.getAppAuditVersionTime(version) ) {
            SysAppConfig sysAppConfig=sysAppConfigService.getSysAppconfig(version);
            if(sysAppConfig!=null && sysAppConfig.getVedioViewIsFee().equals("0")){
                user.setMessagePrice(BigDecimal.valueOf(-1).setScale(2, BigDecimal.ROUND_HALF_UP));
                user.setCallPrice(BigDecimal.valueOf(-1).setScale(2, BigDecimal.ROUND_HALF_UP));
                user.setVideoPrice(BigDecimal.valueOf(-1).setScale(2, BigDecimal.ROUND_HALF_UP));
            }
        }

        return ApiResp.succeed(user);
    }

    @ApiOperation(value = "退出账号")
    @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "String", paramType = "header")
    @PostMapping(value = "/logout")
    ApiResp<Integer> logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getPrincipal().toString();
        User userDo = userService.getUserByUsername(username);
        int ret = userService.logout(userDo);
        return ApiResp.succeed(ret);
    }

    @ApiOperation(value = "判断用户是否已经存在")
    @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "String", paramType = "header")
    @GetMapping(value = "/exist")
    ApiResp<Integer> getUserExist() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getPrincipal().toString();
        User userDo = userService.getUserByUsername(username);
        if (Objects.nonNull(userDo)) {
            return ApiResp.succeed(1);
        } else {
            return ApiResp.succeed(0);
        }
    }

    @ApiOperation(value = "获取用户资产信息")
    @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "String", paramType = "header")
    @GetMapping(value = "/asset")
    ApiResp<Asset> getUserAsset() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getPrincipal().toString();
        User userDo = userService.getUserByUsername(username);

        Asset asset = assetService.getAsset(userDo);
        return ApiResp.succeed(asset);
    }

    @PostMapping(value = "/bind-alipay")
    ApiResp<Asset> bindAlipay(
            @RequestParam("alipayName") String alipayName,
            @RequestParam("alipayNumber") String alipayNumber,
            @RequestParam("phone") String phone) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getPrincipal().toString();
        User userDo = userService.getUserByUsername(username);

        Asset asset = assetService.bindAlipay(userDo, alipayName, alipayNumber, phone);
        return ApiResp.succeed(asset);

    }

    @ApiOperation(value = "请求身份审核")
    @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "String", paramType = "header")
    @PostMapping(value = "/request-identity")
    ApiResp<Integer> requestIdentity(
            @RequestHeader(value = "version", required = false, defaultValue = "v1") String version,
            @RequestParam(value = "type", required = false, defaultValue = "1") Integer type,
            @RequestParam(value = "mode", required = false, defaultValue = "0") Integer mode) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getPrincipal().toString();
        User userDo = userService.getUserByUsername(username);

        // 针对版本审核
        if (sysConfigService.getAppAuditVersion().equals(version)) {
            // 这里只是审核判断，实际值并没有写入用户信息
            if (Objects.isNull(userDo.getMessagePrice())) {
                userDo.setMessagePrice(BigDecimal.valueOf(1).setScale(2, BigDecimal.ROUND_HALF_UP));
            }
            if (Objects.isNull(userDo.getCallPrice())) {
                userDo.setCallPrice(BigDecimal.valueOf(450).setScale(2, BigDecimal.ROUND_HALF_UP));
            }
            if (Objects.isNull(userDo.getVideoPrice())) {
                userDo.setVideoPrice(BigDecimal.valueOf(450).setScale(2, BigDecimal.ROUND_HALF_UP));
            }
        }
        int ret = identityAuditService.requestIdentityAudit(userDo, type, mode);
        return ApiResp.succeed(ret);
    }

    @ApiOperation(value = "请求身份审核(守护版本)")
    @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "String", paramType = "header")
    @PostMapping(value = "/request-identity-ex")
    ApiResp<Integer> requestIdentityEx(
            @RequestHeader(value = "version", required = false, defaultValue = "v1") String version,
            @RequestParam(value = "type", required = false, defaultValue = "1") Integer type,
            @RequestParam(value = "mode", required = false, defaultValue = "0") Integer mode) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getPrincipal().toString();
        User userDo = userService.getUserByUsername(username);

        // 针对版本审核
        if (sysConfigService.getAppAuditVersion().equals(version)) {
            // 这里只是审核判断，实际值并没有写入用户信息
            if (Objects.isNull(userDo.getMessagePrice())) {
                userDo.setMessagePrice(BigDecimal.valueOf(1).setScale(2, BigDecimal.ROUND_HALF_UP));
            }
            if (Objects.isNull(userDo.getCallPrice())) {
                userDo.setCallPrice(BigDecimal.valueOf(450).setScale(2, BigDecimal.ROUND_HALF_UP));
            }
            if (Objects.isNull(userDo.getVideoPrice())) {
                userDo.setVideoPrice(BigDecimal.valueOf(450).setScale(2, BigDecimal.ROUND_HALF_UP));
            }
        }
        int ret = identityAuditService.requestIdentityAuditEx(userDo, type, mode);
        return ApiResp.succeed(ret);
    }

    @ApiOperation(value = "获取最新的身份审核信息")
    @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "String", paramType = "header")
    @GetMapping(value = "/get-identity-info")
    ApiResp<List<IdentityAudit>> getIdentityInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getPrincipal().toString();
        User userDo = userService.getUserByUsername(username);
        List<IdentityAudit> identityAuditList = identityAuditService.getLatestAuditAllType(userDo);
        return ApiResp.succeed(identityAuditList);
    }

    @ApiOperation(value = "通话预检查")
    @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "String", paramType = "header")
    @GetMapping("call-pre-check")
    ApiResp<Integer> callPreCheck(
            @RequestHeader(value = "version", required = false, defaultValue = "v1") String version,
            @RequestParam("digitId") String digitId,
            @RequestParam("type") Integer type ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getPrincipal().toString();
        User userDo = userService.getUserByUsername(username);

        // 审核检查
        if (sysAppConfigService.getAppAuditVersion(version).equals(version) && sysAppConfigService.getAppAuditVersionTime(version) ) {
            SysAppConfig sysAppConfig= sysAppConfigService.getSysAppconfig(version);
            if (sysAppConfig!=null && sysAppConfig.getVedioActualIsFee().equals("0")){
                return ApiResp.succeed(1);
            }

        }

        User userDest = userService.getUserByDigitId(digitId);
        Integer ret = userService.callPreCheck(userDo, userDest, type);
        if (ret == -1) {
            return ApiResp.failed(ret, "对方非主播用户，暂不支持通话");
        } else if (ret == 0) {
            return ApiResp.failed(-10, "聊币余额不足，请尽快充值");
        } else if (ret == -2) {
            return ApiResp.failed(ret, "对方余额不足，无法发起通话");
        } else if (ret == -3) {
            return ApiResp.failed(ret, "主播暂不支持该类型通话");
        } else if (ret == -4) {
            return ApiResp.failed(ret, "主播在休息中，暂不支持通话");
        } else if (ret == -5) {
            return ApiResp.failed(ret, "对方为主播用户，暂不支持通话");
        } else {
            return ApiResp.succeed(ret);
        }
    }

    @ApiOperation(value = "上传设备信息")
    @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "String", paramType = "header")
    @PostMapping("upload-device-info")
    ApiResp<Integer> uploadDeviceInfo(@RequestParam("deviceType") Integer deviceType,
                                      @RequestParam(value = "deviceName", required = false) String deviceName,
                                      @RequestParam(value = "deviceCategory", required = false) String deviceCategory,
                                      @RequestParam(value = "deviceIdfa", required = false) String deviceIdfa,
                                      @RequestParam(value = "deviceImei", required = false) String deviceImei,
                                      @RequestParam(value = "deviceAndroidId", required = false) String deviceAndroidId,
                                      @RequestParam(value = "deviceOaid", required = false) String deviceOaid,
                                      @RequestParam(value = "deviceToken", required = false) String deviceToken,
                                      @RequestParam(value = "physicalDevice", required = false) Integer physicalDevice,
                                      @RequestParam(value = "osVersion", required = false) String osVersion,
                                      @RequestParam(value = "channel", required = false) String channel,
                                      @RequestParam(value = "deviceDescribe", required = false) String deviceDescribe,
                                      @RequestParam(value = "ipAddr", required = false) String ipAddr,
                                      @RequestParam(value = "lang", required = false) String lang,
                                      @RequestParam(value = "appVersion", required = false) String appVersion,
                                      @RequestParam(value = "sysVersion", required = false) String sysVersion,
                                      @RequestParam(value = "deviceBrand", required = false) String deviceBrand,
                                      @RequestParam(value = "deviceIfv", required = false) String deviceIfv,
                                      @RequestParam(value = "localizedmodel", required = false) String localizedmodel,
                                      HttpServletRequest req) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getPrincipal().toString();
        User userDo = userService.getUserByUsername(username);

        int ret = userDeviceService.create(userDo, deviceType, deviceName, deviceCategory,
                deviceIdfa, deviceImei, deviceAndroidId, deviceOaid, deviceToken,
                physicalDevice, osVersion, channel, deviceDescribe, ipAddr, lang,
                appVersion,sysVersion,deviceBrand,deviceIfv,localizedmodel);
        return ApiResp.succeed(ret);
    }

    @ApiOperation(value = "获取用户的收益消费列表")
    @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "String", paramType = "header")
    @GetMapping("profit-consume-list")
    ApiResp<List<ProfitConsumeDetail>> getProfitConsumeList() {
        return ApiResp.succeed(new ArrayList<ProfitConsumeDetail>());
    }

    @ApiOperation(value = "获取用户的守护列表")
    @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "String", paramType = "header")
    @GetMapping("user-guard-list")
    ApiResp<List<GuardDto>> getUserGuardList() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getPrincipal().toString();
        User userDo = userService.getUserByUsername(username);

        List<GuardDto> guardList = guardService.getUserGuardList(userDo);
        return ApiResp.succeed(guardList);
    }
}
