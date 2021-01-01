package com.fmisser.gtc.auth.controller;

import com.fmisser.gtc.auth.domain.User;
import com.fmisser.gtc.auth.service.UserService;
import com.fmisser.gtc.base.dto.auth.TokenDto;
import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.base.response.ApiResp;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.hibernate.validator.constraints.Range;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Size;
import java.security.Principal;
import java.util.Objects;

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

    @ApiOperation(value = "获取当前用户信息以及权限")
    @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "String", paramType = "header")
    @RequestMapping(value = "/current-user", method = RequestMethod.GET)
    public ApiResp<Principal> getCurrentUser(Principal principal) {
        return ApiResp.succeed(principal);
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

    @ApiOperation(value = "禁用/启用账号")
    @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "String", paramType = "header")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @RequestMapping(value = "/enable-account", method = RequestMethod.POST)
    public ApiResp<Integer> enableAccount(@RequestParam("username") String username,
                                       @RequestParam("enable") int enable) {
        return ApiResp.succeed(userService.enableUser(username, enable));
    }

    @ApiOperation(value = "删除账号")
    @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "String", paramType = "header")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @RequestMapping(value = "/delete-account", method = RequestMethod.POST)
    public ApiResp<Integer> deleteAccount(@RequestParam("username") String username) {
        return ApiResp.succeed(userService.deleteUser(username));
    }

    @ApiOperation(value = "编辑业务账号")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "String", paramType = "header"),
            @ApiImplicitParam(name = "username", value = "用户名", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "password", value = "密码", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "password", value = "角色 1：客服， 2：财务， 3：运营", dataType = "Integer", paramType = "query"),
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @RequestMapping(value = "/edit-account", method = RequestMethod.POST)
    public ApiResp<User> editAccount(@RequestParam("username") String username,
                                        @RequestParam(value = "password", required = false) @Size(min = 6, max = 16) String password,
                                        @RequestParam(value = "role", required = false) Integer role) {
        String roleName = null;
        if (Objects.nonNull(role)) {
            if (role == 1) {
                roleName = "ROLE_CUSTOMER_SERVICE";
            } else if (role == 2) {
                roleName = "ROLE_FINANCE";
            } else if (role == 3) {
                roleName = "ROLE_OPERATE";
            }
        }

        return ApiResp.succeed(userService.editUser(username, password, roleName));
    }

    @ApiOperation(value = "创建业务账号")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "String", paramType = "header"),
            @ApiImplicitParam(name = "username", value = "用户名", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "password", value = "密码", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "password", value = "角色 1：客服， 2：财务， 3：运营", dataType = "Integer", paramType = "query"),
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @RequestMapping(value = "/create-account", method = RequestMethod.POST)
    public ApiResp<User> createAccount(@RequestParam("username") String username,
                                     @RequestParam(value = "password") @Size(min = 6, max = 16) String password,
                                     @RequestParam(value = "role") @Range(min = 1, max = 3) Integer role) {
        String roleName = null;
        if (Objects.nonNull(role)) {
            if (role == 1) {
                roleName = "ROLE_CUSTOMER_SERVICE";
            } else if (role == 2) {
                roleName = "ROLE_FINANCE";
            } else if (role == 3) {
                roleName = "ROLE_OPERATE";
            }
        }

        return ApiResp.succeed(userService.create(username, password, roleName));
    }
}

