package com.fmisser.gtc.auth.controller;

import com.fmisser.gtc.auth.domain.User;
import com.fmisser.gtc.auth.service.UserService;
import com.fmisser.gtc.base.dto.auth.TokenDto;
import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.base.response.ApiResp;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Size;

@Api(description = "管理账户中心")
@RestController
@RequestMapping("/cms")
@Validated
public class CmsController {

    private final UserService userService;

    public CmsController(@Qualifier("top") UserService userService) {
        this.userService = userService;
    }

    @ApiOperation(value = "创建用户")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public User create(@RequestParam("username") String username,
                       @RequestParam("password") @Size(min = 6, max = 16) String pwd) {
        return userService.create(username, pwd, "ROLE_UNDEFINE");
    }

    @ApiOperation(value = "账户密码登录")
    @PostMapping("/login")
    public ApiResp<TokenDto> login(@RequestParam("username")  String username,
                                   @RequestParam("password") String password) throws ApiException {
        return ApiResp.succeed(userService.login(username, password));
    }

    @ApiOperation(value = "创建管理员角色账户(需要超管权限)")
    @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "String", paramType = "header")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @RequestMapping(value = "/create-manager", method = RequestMethod.POST)
    public ApiResp<User> createManager(@RequestParam("username") String username,
                              @RequestParam("password") @Size(min = 6, max = 16) String password) {
        return ApiResp.succeed(userService.create(username, password, "ROLE_MANAGER"));
    }

    @ApiOperation(value = "创建客服角色账户(需要管理者权限)")
    @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "String", paramType = "header")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @RequestMapping(value = "/create-customer-service", method = RequestMethod.POST)
    public ApiResp<User> createCustomerService(@RequestParam("username") String username,
                                      @RequestParam("password") @Size(min = 6, max = 16) String password) {
        return ApiResp.succeed(userService.create(username, password, "ROLE_CUSTOMER_SERVICE"));
    }

    @ApiOperation(value = "创建财务角色账户(需要管理者权限)")
    @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "String", paramType = "header")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @RequestMapping(value = "/create-finance", method = RequestMethod.POST)
    public ApiResp<User> createFinance(@RequestParam("username") String username,
                              @RequestParam("password") @Size(min = 6, max = 16) String password) {
        return ApiResp.succeed(userService.create(username, password, "ROLE_FINANCE"));
    }

    @ApiOperation(value = "创建运营角色账户(需要管理者权限)")
    @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "String", paramType = "header")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @RequestMapping(value = "/create-operate", method = RequestMethod.POST)
    public ApiResp<User> createOperate(@RequestParam("username") String username,
                              @RequestParam("password") @Size(min = 6, max = 16) String password) {
        return ApiResp.succeed(userService.create(username, password, "ROLE_OPERATE"));
    }
}

