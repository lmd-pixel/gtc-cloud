package com.fmisser.gtc.social.controller;

import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.base.response.ApiResp;
import com.fmisser.gtc.social.domain.User;
import com.fmisser.gtc.social.service.GreetService;
import com.fmisser.gtc.social.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(description = "打招呼管理")
@RestController
@RequestMapping("/greet-manager")
@Validated
public class GreetManagerController {

    private final GreetService greetService;

    private final UserService userService;

    public GreetManagerController(GreetService greetService, UserService userService) {
        this.greetService = greetService;
        this.userService = userService;
    }

    @PostMapping("/greet")
    @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "String", paramType = "header")
    @PreAuthorize("hasAnyRole('MANAGER')")
    ApiResp<Integer> greet(@RequestParam("digitId") String digitId) {
        User user = userService.getUserByDigitId(digitId);
        int ret = greetService.createGreet(user);

        return ApiResp.succeed(ret);
    }
}
