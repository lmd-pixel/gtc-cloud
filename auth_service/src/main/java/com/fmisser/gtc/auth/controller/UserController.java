package com.fmisser.gtc.auth.controller;

import com.fmisser.gtc.auth.service.SmsService;
import com.fmisser.gtc.auth.service.UserService;
import com.fmisser.gtc.base.dto.auth.TokenDto;
import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.base.response.ApiResp;
import com.fmisser.gtc.base.response.ApiRespHelper;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Size;

@Api(description = "平台账户中心")
@RestController
@RequestMapping("/user")
@Validated
public class UserController {

    private final UserService userService;

    private final SmsService smsService;

    private final ApiRespHelper apiRespHelper;

    public UserController(@Qualifier("top") UserService userService,
                          SmsService smsService,
                          ApiRespHelper apiRespHelper) {
        this.userService = userService;
        this.smsService = smsService;
        this.apiRespHelper = apiRespHelper;
    }

    @PostMapping("/auto-login")
    public ApiResp<TokenDto> autoLogin(@RequestHeader(value = "identity", required = false) String identity,
//                                       @RequestParam(value = "phone", required = false) @Size(min = 11, max = 11, message = "请输入有效的手机号") String phone,
                                       @RequestParam("token") String token,
                                       @RequestParam(value = "ipAddress",required = false) String ipAddress,
                                       @RequestParam(value ="deviceId",required = false) String deviceId) throws ApiException {
        try {
            return ApiResp.succeed(userService.autoLogin(identity, token,ipAddress,deviceId));
        } catch (Exception e) {
            return ApiResp.failed(-1, "你暂时无法登录");

        }

    }

    @PostMapping("/sms-login")
    public ApiResp<TokenDto> phoneCodeLogin(@RequestParam("phone") @Size(min = 11, max = 11, message = "请输入有效的手机号") String phone,
                                            @RequestParam("code") String code,
                                            @RequestParam(value = "ipAddress",required = false) String ipAddress,
                                            @RequestParam(value ="deviceId",required = false) String deviceId
                                            ) throws ApiException {
        try {
            return ApiResp.succeed(userService.smsLogin(phone, code,ipAddress,deviceId));
        } catch (Exception e) {
            if(!e.getMessage().equals("LOGINFAIL")){
                return ApiResp.failed(-1, "验证码不正确，请重试!");
            }else{
                return ApiResp.failed(-1, "你暂时无法登录");
            }

        }
    }

    @PostMapping("/apple-login")
    public ApiResp<TokenDto> appleLogin(@RequestHeader(value = "identity", required = false) String identity,
                                        @RequestParam("subject") String subject,
                                        @RequestParam("token") String token) throws ApiException {
        return ApiResp.succeed(userService.appleLogin(subject, token));
    }

    @PostMapping("/wx-login")
    public ApiResp<TokenDto> appleLogin(@RequestParam("unionid") String unionid) throws ApiException {
        return ApiResp.succeed(userService.wxLogin(unionid));
    }

    @PostMapping("/goole-login")
    public ApiResp<TokenDto> gooleLogin(@RequestParam("code") String code,
                                        @RequestParam(value = "token", required = false) String token) throws ApiException {
        return ApiResp.succeed(userService.gooleLogin(code,token));
    }


    @PostMapping("/refresh-token")
    public ApiResp<TokenDto> refreshToken(@RequestParam("refreshToken") String refreshToken) throws ApiException {
        return ApiResp.succeed(userService.refreshToken(refreshToken));
    }




    @PostMapping("/phone-code")
    public ApiResp<String> sendPhoneCode(@RequestParam("phone") @Size(min = 11, max = 11, message = "请输入有效的手机号") String phone,
                                         @RequestParam("type") int type) throws ApiException {
        boolean ret = smsService.sendPhoneCode(phone, type);
        if (ret) {
            return ApiResp.succeed("");
        } else {
            return apiRespHelper.error();
        }
    }


}
