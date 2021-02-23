package com.fmisser.gtc.social.controller;

import com.fmisser.gtc.base.dto.social.AnchorDto;
import com.fmisser.gtc.base.dto.social.ConsumerDto;
import com.fmisser.gtc.base.dto.social.RecommendDto;
import com.fmisser.gtc.base.dto.social.calc.CalcUserDto;
import com.fmisser.gtc.base.response.ApiResp;
import com.fmisser.gtc.social.domain.IdentityAudit;
import com.fmisser.gtc.social.domain.User;
import com.fmisser.gtc.social.service.UserManagerService;
import com.fmisser.gtc.social.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.hibernate.validator.constraints.Range;
import org.springframework.data.util.Pair;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.math.BigDecimal;
import java.util.*;

@Api(description = "用户管理")
@RestController
@RequestMapping("/user-manager")
@Validated
public class UserManagerController {
    private final UserManagerService userManagerService;
    private final UserService userService;

    public UserManagerController(UserManagerService userManagerService,
                                 UserService userService) {
        this.userManagerService = userManagerService;
        this.userService = userService;
    }

    @ApiOperation(value = "获取主播用户列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "String", paramType = "header"),
            @ApiImplicitParam(name = "digitId", value = "用户ID", paramType = "query", required = false),
            @ApiImplicitParam(name = "nick", value = "用户昵称", paramType = "query", required = false),
            @ApiImplicitParam(name = "phone", value = "用户手机号", paramType = "query", required = false),
            @ApiImplicitParam(name = "gender", value = "用户性别", paramType = "query", dataType = "Integer, required = false"),
            @ApiImplicitParam(name = "startTime", value = "起始时间", paramType = "query", dataType = "date", required = false),
            @ApiImplicitParam(name = "endTime", value = "结束时间", paramType = "query", dataType = "date", required = false),
            @ApiImplicitParam(name = "pageIndex", value = "展示第几页", paramType = "query", defaultValue = "1", dataType = "Integer"),
            @ApiImplicitParam(name = "pageSize", value = "每页数据条数", paramType = "query", defaultValue = "30", dataType = "Integer"),
            @ApiImplicitParam(name = "sortColumn",
                    value = "排序列 0：注册时间， 1：最近登录时间, 2: 账户余额， 3：礼物收益， 4：私信收益， 5：语音收益 6：语音时长  7：视频收益 8：视频时长 ",
                    paramType = "query", defaultValue = "0", dataType = "Integer"),
            @ApiImplicitParam(name = "sortDirection",
                    value = "排序方向 0：AS， 1： DESC",
                    paramType = "query", defaultValue = "0", dataType = "Integer")})
    @PreAuthorize("hasAnyRole('MANAGER')")
    @RequestMapping(value = "/list-anchor", method = RequestMethod.GET)
    public ApiResp<List<AnchorDto>> getAnchorList(@RequestParam(value = "digitId", required = false) String digitId,
                                                      @RequestParam(value = "nick", required = false) String nick,
                                                      @RequestParam(value = "phone", required = false) String phone,
                                                      @RequestParam(value = "gender", required = false) Integer gender,
                                                      @RequestParam(value = "startTime", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date startTime,
                                                      @RequestParam(value = "endTime", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date endTime,
                                                      @RequestParam(value = "pageIndex", required = false, defaultValue = "1") int pageIndex,
                                                      @RequestParam(value = "pageSize", required = false, defaultValue = "30") int pageSize,
                                                      @RequestParam(value = "sortColumn", required = false, defaultValue = "0") int sortColumn,
                                                      @RequestParam(value = "sortDirection", required = false, defaultValue = "0") int sortDirection) {
        Pair<List<AnchorDto>, Map<String, Object>> anchorDtoList = userManagerService
                .getAnchorList(digitId, nick, phone, gender, startTime, endTime, pageIndex, pageSize, sortColumn, sortDirection);

        return ApiResp.succeed(anchorDtoList.getFirst(), anchorDtoList.getSecond());
    }

    @ApiOperation(value = "获取普通用户列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "String", paramType = "header"),
            @ApiImplicitParam(name = "digitId", value = "用户ID", paramType = "query", required = false),
            @ApiImplicitParam(name = "nick", value = "用户昵称", paramType = "query", required = false),
            @ApiImplicitParam(name = "phone", value = "用户手机号", paramType = "query", required = false),
            @ApiImplicitParam(name = "startTime", value = "起始时间", paramType = "query", dataType = "date", required = false),
            @ApiImplicitParam(name = "endTime", value = "结束时间", paramType = "query", dataType = "date", required = false),
            @ApiImplicitParam(name = "pageIndex", value = "展示第几页", paramType = "query", defaultValue = "1", dataType = "Integer"),
            @ApiImplicitParam(name = "pageSize", value = "每页数据条数", paramType = "query", defaultValue = "30", dataType = "Integer"),
            @ApiImplicitParam(name = "sortColumn",
                    value = "排序列 0：注册时间， 1：最近登录时间, 2: 礼物消费， 3：私信消费， 4：语音消费， 5：视频消费， 6：充值总额， 7：账户余额 ",
                    paramType = "query", defaultValue = "0", dataType = "Integer"),
            @ApiImplicitParam(name = "sortDirection",
                    value = "排序方向 0：AS， 1： DESC",
                    paramType = "query", defaultValue = "0", dataType = "Integer")})
    @PreAuthorize("hasAnyRole('MANAGER')")
    @RequestMapping(value = "/list-consumer", method = RequestMethod.GET)
    public ApiResp<List<ConsumerDto>> getConsumerList(@RequestParam(value = "digitId", required = false) String digitId,
                                             @RequestParam(value = "nick", required = false) String nick,
                                             @RequestParam(value = "phone", required = false) String phone,
                                             @RequestParam(value = "startTime", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date startTime,
                                             @RequestParam(value = "endTime", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date endTime,
                                             @RequestParam(value = "pageIndex", required = false, defaultValue = "1") int pageIndex,
                                             @RequestParam(value = "pageSize", required = false, defaultValue = "30") int pageSize,
                                             @RequestParam(value = "sortColumn", required = false, defaultValue = "0") int sortColumn,
                                             @RequestParam(value = "sortDirection", required = false, defaultValue = "0") int sortDirection) {
        Pair<List<ConsumerDto>, Map<String, Object>> consumerDtoList = userManagerService
                .getConsumerList(digitId, nick, phone, startTime, endTime, pageIndex, pageSize, sortColumn, sortDirection);

        // 统计数据
//        Map<String, Object> countMap = new HashMap<>();
//        BigDecimal countRecharge = consumerDtoList
//                .stream()
//                .map(ConsumerDto::getRechargeCoin)
//                .filter(Objects::nonNull)
//                .reduce( BigDecimal.ZERO, BigDecimal::add );

//        countMap.put("userCount", consumerDtoList.size());
//        countMap.put("userRecharge", countRecharge);

        return ApiResp.succeed(consumerDtoList.getFirst(), consumerDtoList.getSecond());
    }

    @ApiOperation(value = "获取用户信息(包含照片信息)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "String", paramType = "header"),
            @ApiImplicitParam(name = "digitId", value = "用户ID", paramType = "query")
    })
    @GetMapping(value = "/profile")
    @PreAuthorize("hasAnyRole('MANAGER')")
    ApiResp<User> getProfile(@RequestParam(value = "digitId") String digitId) {
        User user = userManagerService.getUserProfile(digitId);
        return ApiResp.succeed(user);
    }

    @ApiOperation(value = "获取推荐主播列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "String", paramType = "header"),
            @ApiImplicitParam(name = "digitId", value = "用户ID", paramType = "query"),
            @ApiImplicitParam(name = "nick", value = "用户昵称", paramType = "query"),
            @ApiImplicitParam(name = "type", value = "推荐模块 0: 首页推荐 1： 首页活跃（保留，暂时不做）2：首页新人 3：私聊推荐主播 4： 通话推荐主播", required = true, paramType = "query"),
            @ApiImplicitParam(name = "pageIndex", value = "展示第几页", paramType = "query", defaultValue = "1", dataType = "Integer"),
            @ApiImplicitParam(name = "pageSize", value = "每页数据条数", paramType = "query", defaultValue = "30", dataType = "Integer")
    })
    @GetMapping(value = "/list-recommend")
    @PreAuthorize("hasAnyRole('MANAGER')")
    ApiResp<List<RecommendDto>> getRecommendList(@RequestParam(value = "digitId", required = false) String digitId,
                                                 @RequestParam(value = "nick", required = false) String nick,
                                                 @RequestParam(value = "gender", required = false) Integer gender,
                                                 @RequestParam(value = "type") @Range(min = 0, max = 4, message = "type参数范围不合法") Integer type,
                                                 @RequestParam(value = "pageIndex", required = false, defaultValue = "1") int pageIndex,
                                                 @RequestParam(value = "pageSize", required = false, defaultValue = "30") int pageSize) {
        Pair<List<RecommendDto>, Map<String, Object>> recommendDtoList =
                userManagerService.getRecommendList(digitId, nick, gender, type, pageIndex, pageSize);
        return ApiResp.succeed(recommendDtoList.getFirst(), recommendDtoList.getSecond());
    }

    @ApiOperation(value = "设置主播推荐")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "String", paramType = "header"),
            @ApiImplicitParam(name = "digitId", value = "用户ID", paramType = "query", required = true),
            @ApiImplicitParam(name = "type", value = "推荐模块 0: 首页推荐 1： 首页活跃（保留，暂时不做）2：首页新人 3：私聊推荐主播 4： 通话推荐主播", paramType = "query", defaultValue = "0", dataType = "Integer"),
            @ApiImplicitParam(name = "recommend", value = "是否推荐 0：取消推荐 1：设置成推荐", paramType = "query", defaultValue = "0", dataType = "Integer"),
            @ApiImplicitParam(name = "level", value = "推荐排序数值 如果取消推荐，这个字段可以不填", paramType = "query", dataType = "Integer")
    })
    @PostMapping(value = "/config-recommend")
    ApiResp<Integer> configRecommend(@RequestParam(value = "digitId") String digitId,
                                     @RequestParam(value = "type") @Range(min = 0, max = 4, message = "type参数范围不合法") int type,
                                     @RequestParam(value = "recommend") @Range(min = 0, max = 1, message = "recommend参数范围不合法") int recommend,
                                     @RequestParam(value = "level", required = false) Long level) {
        // check params
        if (recommend == 1 && Objects.isNull(level)) {
            return ApiResp.failed(-1, "排序值未设置");
        }

        int ret = userManagerService.configRecommend(digitId, type, recommend, level);
        return ApiResp.succeed(ret, "设置成功");
    }


    @ApiOperation(value = "获取主播认证审核列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "String", paramType = "header"),
            @ApiImplicitParam(name = "digitId", value = "用户ID", paramType = "query"),
            @ApiImplicitParam(name = "nick", value = "用户昵称", paramType = "query"),
            @ApiImplicitParam(name = "gender", value = "用户性别", paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "status", value = "审核状态 0: 审核中 1：审核未通过 2：全部(默认)", paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "startTime", value = "起始时间", paramType = "query", dataType = "date"),
            @ApiImplicitParam(name = "endTime", value = "结束时间", paramType = "query", dataType = "date"),
            @ApiImplicitParam(name = "pageIndex", value = "展示第几页", paramType = "query", defaultValue = "1", dataType = "Integer"),
            @ApiImplicitParam(name = "pageSize", value = "每页数据条数", paramType = "query", defaultValue = "30", dataType = "Integer")
    })
    @GetMapping(value = "/list-anchor-audit")
    @PreAuthorize("hasAnyRole('MANAGER')")
    ApiResp<List<IdentityAudit>> getAnchorAuditList(@RequestParam(value = "digitId", required = false) String digitId,
                                                 @RequestParam(value = "nick", required = false) String nick,
                                                 @RequestParam(value = "gender", required = false) Integer gender,
                                                 @RequestParam(value = "status", required = false, defaultValue = "2") @Range(min = 0, max = 2) Integer status,
                                                 @RequestParam(value = "startTime", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date startTime,
                                                 @RequestParam(value = "endTime", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date endTime,
                                                 @RequestParam(value = "pageIndex", required = false, defaultValue = "1") int pageIndex,
                                                 @RequestParam(value = "pageSize", required = false, defaultValue = "30") int pageSize) {
        Pair<List<IdentityAudit>, Map<String, Object>> identityAuditList = userManagerService
                .getAnchorAuditList(digitId, nick, gender, status, startTime, endTime, pageIndex, pageSize);
        return ApiResp.succeed(identityAuditList.getFirst(), identityAuditList.getSecond());
    }

    @ApiOperation(value = "获取主播最新的所有类型审核")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "String", paramType = "header"),
            @ApiImplicitParam(name = "digitId", value = "用户ID", paramType = "query")
    })
    @GetMapping(value = "/get-anchor-audit")
    @PreAuthorize("hasAnyRole('MANAGER')")
    ApiResp<List<IdentityAudit>> getAnchorAudit(@RequestParam(value = "digitId") String digitId ) {
        List<IdentityAudit> identityAuditList = userManagerService.getAnchorAudit(digitId);
        return ApiResp.succeed(identityAuditList);
    }

    @ApiOperation(value = "主播认证审核")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "String", paramType = "header"),
            @ApiImplicitParam(name = "serialNumber", value = "申请编号", paramType = "query", required = true),
            @ApiImplicitParam(name = "operate", value = "审核操作： 0: 审核不通过 1：审核通过", paramType = "query", defaultValue = "0", dataType = "Integer"),
            @ApiImplicitParam(name = "message", value = "审核备注信息", paramType = "query")
    })
    @PostMapping(value = "/anchor-audit")
    ApiResp<Integer> anchorAudit(@RequestParam(value = "serialNumber") String serialNumber,
                                 @RequestParam(value = "operate") @Range(min = 0, max = 1) int operate,
                                 @RequestParam(value = "message", required = false) String message) {
        int ret = userManagerService.anchorAudit(serialNumber, operate, message);
        return ApiResp.succeed(ret, "设置成功");
    }

    @ApiOperation(value = "获取用户统计数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "String", paramType = "header"),
            @ApiImplicitParam(name = "startTime", value = "起始时间", paramType = "query", dataType = "date"),
            @ApiImplicitParam(name = "endTime", value = "结束时间", paramType = "query", dataType = "date"),
            @ApiImplicitParam(name = "pageIndex", value = "展示第几页", paramType = "query", defaultValue = "1", dataType = "Integer"),
            @ApiImplicitParam(name = "pageSize", value = "每页数据条数", paramType = "query", defaultValue = "30", dataType = "Integer")
    })
    @GetMapping(value = "/calc-user-list")
    @PreAuthorize("hasAnyRole('MANAGER')")
    ApiResp<List<CalcUserDto>> getCalcUser(
            @RequestParam(value = "startTime", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date startTime,
            @RequestParam(value = "endTime", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date endTime,
            @RequestParam(value = "pageIndex", required = false, defaultValue = "1") int pageIndex,
            @RequestParam(value = "pageSize", required = false, defaultValue = "30") int pageSize) {
        Pair<List<CalcUserDto>, Map<String, Object>> calcUserDtoPair = userManagerService
                .getCalcUser(startTime, endTime, pageIndex, pageSize);

        return ApiResp.succeed(calcUserDtoPair.getFirst(), calcUserDtoPair.getSecond());
    }

    @ApiOperation(value = "更新用户信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "String", paramType = "header")
    })
    @PostMapping(value = "/update-user-profile")
    @PreAuthorize("hasAnyRole('MANAGER')")
    ApiResp<User> uploadProfile(MultipartHttpServletRequest request,
                                @RequestParam(value = "digitId") String digitId,
                                @RequestParam(value = "nick", required = false) String nick,
                                @RequestParam(value = "birth", required = false) String birth,
                                @RequestParam(value = "city", required = false) String city,
                                @RequestParam(value = "profession", required = false) String profession,
                                @RequestParam(value = "intro", required = false) String intro,
                                @RequestParam(value = "labels", required = false) String labels,
                                @RequestParam(value = "callPrice", required = false) String callPrice,
                                @RequestParam(value = "videoPrice", required = false) String videoPrice,
                                @RequestParam(value = "messagePrice", required = false) String messagePrice) {

        User userDo = userService.getUserByDigitId(digitId);

        User user = userService.updateProfile(userDo, nick, birth, city, profession,
                intro, labels, callPrice, videoPrice, messagePrice, request.getFileMap());

        return ApiResp.succeed(user);
    }

    @ApiOperation(value = "更新用户信息-基础数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "String", paramType = "header")
    })
    @PostMapping(value = "/update-user-profile-base")
    @PreAuthorize("hasAnyRole('MANAGER')")
    ApiResp<User> uploadProfileBase(
                                @RequestParam(value = "digitId") String digitId,
                                @RequestParam(value = "nick", required = false) String nick,
                                @RequestParam(value = "birth", required = false) String birth,
                                @RequestParam(value = "city", required = false) String city,
                                @RequestParam(value = "profession", required = false) String profession,
                                @RequestParam(value = "intro", required = false) String intro,
                                @RequestParam(value = "labels", required = false) String labels,
                                @RequestParam(value = "callPrice", required = false) String callPrice,
                                @RequestParam(value = "videoPrice", required = false) String videoPrice,
                                @RequestParam(value = "messagePrice", required = false) String messagePrice) {

        User userDo = userService.getUserByDigitId(digitId);

        Map<String, MultipartFile> multipartFileMap = new HashMap<>();
        User user = userService.updateProfile(userDo, nick, birth, city, profession,
                intro, labels, callPrice, videoPrice, messagePrice, multipartFileMap);

        return ApiResp.succeed(user);
    }

}
