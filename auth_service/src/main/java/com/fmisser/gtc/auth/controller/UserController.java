package com.fmisser.gtc.auth.controller;

import com.fmisser.gtc.auth.domain.User;
import com.fmisser.gtc.auth.service.UserService;
import io.swagger.annotations.Api;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.constraints.Size;
import java.security.Principal;

@Api("用户中心")
@RestController
@RequestMapping("/user")
@Validated
public class UserController {

    @Resource
    private UserService userService;

    @RequestMapping(value = "/current", method = RequestMethod.GET)
    public Principal principal(Principal principal) {
        return principal;
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public User create(@RequestParam("username") String username,
                       @RequestParam("password") @Size(min = 6, max = 16) String pwd) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(pwd);
        return userService.create(user);
    }
}
