package com.fmisser.gtc.social.controller;

import com.fmisser.gtc.base.response.ApiResp;
import com.fmisser.gtc.social.domain.User;
import com.fmisser.gtc.social.service.ImService;
import com.fmisser.gtc.social.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Api(description = "聊天API")
@RestController
@RequestMapping("/im")
public class ImController {

    @Autowired
    private ImService imService;

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "String", paramType = "header")
    public ApiResp<String> login() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getPrincipal().toString();
        User userDo = userService.getUserByUsername(username);

        String userSig = imService.login(userDo);
        return ApiResp.succeed(userSig);
    }

    @PostMapping("/logout")
    @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "String", paramType = "header")
    public ApiResp<String> logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getPrincipal().toString();
        User userDo = userService.getUserByUsername(username);

        String userSig = imService.login(userDo);
        return ApiResp.succeed(userSig);
    }

    @PostMapping("/update-call")
    @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "String", paramType = "header")
    public ApiResp<Map<String, Object>> updateCall(
            @RequestHeader(value = "version", required = false, defaultValue = "v1") String version,
            @RequestParam("roomId") Long roomId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getPrincipal().toString();
        User userDo = userService.getUserByUsername(username);

        Map<String, Object> results = imService.updateCall(userDo, roomId, version);
        return ApiResp.succeed(results);
    }

    @PostMapping("/call")
    @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "String", paramType = "header")
    public ApiResp<Long> call(
            @RequestHeader(value = "version", required = false, defaultValue = "v1") String version,
            @RequestParam("toUserDigitId") String toUserDigitId,
            @RequestParam("type") int type) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getPrincipal().toString();
        User userDo = userService.getUserByUsername(username);

        User userTo = userService.getUserByDigitId(toUserDigitId);

        Long roomId = imService.call(userDo, userTo, type);
        return ApiResp.succeed(roomId);
    }

    @PostMapping("/accept-call")
    @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "String", paramType = "header")
    public ApiResp<Integer> acceptCall(
            @RequestHeader(value = "version", required = false, defaultValue = "v1") String version,
            @RequestParam("toUserDigitId") String toUserDigitId,
            @RequestParam("roomId") Long roomId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getPrincipal().toString();
        User userDo = userService.getUserByUsername(username);

        User userTo = null;
        if (!StringUtils.isEmpty(toUserDigitId)) {
            userTo = userService.getUserByDigitId(toUserDigitId);
        }

        int ret = imService.accept(userDo, userTo, roomId);
        return ApiResp.succeed(ret);
    }

    @PostMapping("/hangup-call")
    @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "String", paramType = "header")
    public ApiResp<Map<String, Object>> hangupCall(
            @RequestHeader(value = "version", required = false, defaultValue = "v1") String version,
            @RequestParam("roomId") Long roomId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getPrincipal().toString();
        User userDo = userService.getUserByUsername(username);

//        User userTo = null;
//        if (!StringUtils.isEmpty(toUserDigitId)) {
//            userTo = userService.getUserByDigitId(toUserDigitId);
//        }

        Map<String, Object> ret = imService.hangup(userDo, roomId, version);
        return ApiResp.succeed(ret);
    }

    @PostMapping("/call-gen")
    @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "String", paramType = "header")
    public ApiResp<Long> callGen(
            @RequestHeader(value = "version", required = false, defaultValue = "v1") String version,
            @RequestParam("toUserDigitId") String toUserDigitId,
            @RequestParam("type") int type) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getPrincipal().toString();
        User userDo = userService.getUserByUsername(username);

        User userTo = userService.getUserByDigitId(toUserDigitId);

        Long roomId = imService.callGen(userDo, userTo, type);
        return ApiResp.succeed(roomId);
    }

    @PostMapping("/accept-gen")
    @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "String", paramType = "header")
    public ApiResp<Integer> acceptGen(
            @RequestHeader(value = "version", required = false, defaultValue = "v1") String version,
            @RequestParam("roomId") Long roomId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getPrincipal().toString();
        User userDo = userService.getUserByUsername(username);

        int ret = imService.acceptGen(userDo, roomId);
        return ApiResp.succeed(ret);
    }

    @PostMapping("/invite-gen")
    @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "String", paramType = "header")
    public ApiResp<Integer> inviteGen(
            @RequestHeader(value = "version", required = false, defaultValue = "v1") String version,
            @RequestParam("roomId") Long roomId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getPrincipal().toString();
        User userDo = userService.getUserByUsername(username);

        Integer ret = imService.inviteGen(userDo, roomId);
        return ApiResp.succeed(ret);
    }

    @PostMapping("/reject-gen")
    @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "String", paramType = "header")
    public ApiResp<Integer> rejectGen(
            @RequestHeader(value = "version", required = false, defaultValue = "v1") String version,
            @RequestParam("roomId") Long roomId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getPrincipal().toString();
        User userDo = userService.getUserByUsername(username);

        Integer ret = imService.rejectGen(userDo, roomId);
        return ApiResp.succeed(ret);
    }

    @PostMapping("/hangup-gen")
    @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "String", paramType = "header")
    public ApiResp<Integer> hangupGen(
            @RequestHeader(value = "version", required = false, defaultValue = "v1") String version,
            @RequestParam("roomId") Long roomId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getPrincipal().toString();
        User userDo = userService.getUserByUsername(username);

        Integer ret = imService.hangupGen(userDo, roomId);
        return ApiResp.succeed(ret);
    }

    @GetMapping("/update-gen")
    @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "String", paramType = "header")
    public ApiResp<Map<String, Object>> updateGen(
            @RequestHeader(value = "version", required = false, defaultValue = "v1") String version,
            @RequestParam("roomId") Long roomId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getPrincipal().toString();
        User userDo = userService.getUserByUsername(username);

        return ApiResp.succeed(imService.updateGen(userDo, roomId));
    }

    @GetMapping("/result-gen")
    @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "String", paramType = "header")
    public ApiResp<Map<String, Object>> resultGen(
            @RequestHeader(value = "version", required = false, defaultValue = "v1") String version,
            @RequestParam("roomId") Long roomId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getPrincipal().toString();
        User userDo = userService.getUserByUsername(username);

        return ApiResp.succeed(imService.resultGen(userDo, roomId));
    }

    @GetMapping("/user-sig")
    @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "String", paramType = "header")
    public ApiResp<String> getUserSig() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getPrincipal().toString();
        User userDo = userService.getUserByUsername(username);

        String userSign = imService.genUserSign(userDo);

        return ApiResp.succeed(userSign);
    }

    @PostMapping("/send-msg")
    @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "String", paramType = "header")
    public ApiResp<Integer> sendMsg(@RequestParam("digitId") String digitId,
                                    @RequestParam("content") String content) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getPrincipal().toString();
        User userDo = userService.getUserByUsername(username);

        User toUser = userService.getUserByDigitId(digitId);

        return ApiResp.succeed(imService.sendToUser(userDo, toUser, content));
    }

    @PostMapping("/system-send-msg")
    @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "String", paramType = "header")
    @PreAuthorize("hasAnyRole('MANAGER')")
    public ApiResp<Integer> systemSendMsg(@RequestParam(value = "digitIdFrom", required = false) String digitIdFrom,
                                          @RequestParam("digitIdTo") String digitIdTo,
                                          @RequestParam("content") String content) {

        User userFrom = null;
        if (!StringUtils.isEmpty(digitIdFrom)) {
            userFrom = userService.getUserByDigitId(digitIdFrom);
        }

        User toUser = userService.getUserByDigitId(digitIdTo);

        return ApiResp.succeed(imService.sendToUser(userFrom, toUser, content));
    }
}
