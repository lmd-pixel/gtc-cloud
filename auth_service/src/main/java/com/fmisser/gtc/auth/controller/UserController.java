package com.fmisser.gtc.auth.controller;

import com.fmisser.gtc.auth.domain.User;
import com.fmisser.gtc.auth.service.SmsService;
import com.fmisser.gtc.auth.service.UserService;
import com.fmisser.gtc.base.dto.auth.TokenDto;
import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.base.response.ApiResp;
import com.fmisser.gtc.base.response.ApiRespHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.Size;
import java.security.Principal;

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
    public ApiResp<TokenDto> autoLogin(@RequestParam(value = "phone", required = false) @Size(min = 11, max = 11, message = "请输入有效的手机号") String phone,
                                       @RequestParam("token") String token) throws ApiException {
        return ApiResp.succeed(userService.autoLogin(phone, token));
    }

    @PostMapping("/sms-login")
    public ApiResp<TokenDto> phoneCodeLogin(@RequestParam("phone") @Size(min = 11, max = 11, message = "请输入有效的手机号") String phone,
                                            @RequestParam("code") String code) throws ApiException {
        try {
            return ApiResp.succeed(userService.smsLogin(phone, code));
        } catch (Exception e) {
            return ApiResp.failed(-1, "验证码不正确，请重试!");
        }
    }

    @PostMapping("/apple-login")
    public ApiResp<TokenDto> appleLogin(@RequestHeader("identity") String identity,
                                        @RequestParam("subject") String subject,
                                        @RequestParam("token") String token) throws ApiException {
        return ApiResp.succeed(userService.appleLogin(subject, token));
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
