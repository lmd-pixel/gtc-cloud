package com.fmisser.gtc.auth.controller;

import com.fmisser.gtc.auth.domain.User;
import com.fmisser.gtc.auth.service.SmsService;
import com.fmisser.gtc.auth.service.UserService;
import com.fmisser.gtc.base.dto.auth.TokenDto;
import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.base.response.ApiResp;
import com.fmisser.gtc.base.response.ApiRespHelper;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.Size;
import java.security.Principal;

@Api("用户中心")
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

    @RequestMapping(value = "/current", method = RequestMethod.GET)
    public Principal principal(Principal principal) {
        return principal;
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public User create(@RequestParam("username") String username,
                       @RequestParam("password") @Size(min = 6, max = 16) String pwd) {
        return userService.create(username, pwd);
    }

    @PostMapping("/auto-login")
    public ApiResp<TokenDto> autoLogin(@RequestParam("phone") @Size(min = 11, max = 11, message = "请输入有效的手机号") String phone,
                                       @RequestParam("token") String token) throws ApiException {
        return userService.autoLogin(phone, token);
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
