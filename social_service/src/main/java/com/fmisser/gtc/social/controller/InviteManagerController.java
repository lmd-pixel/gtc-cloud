package com.fmisser.gtc.social.controller;

import com.fmisser.gtc.base.response.ApiResp;
import com.fmisser.gtc.social.domain.User;
import com.fmisser.gtc.social.service.InviteService;
import com.fmisser.gtc.social.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(description = "邀请 API")
@RestController
@RequestMapping("/invite-manager")
public class InviteManagerController {
    @Autowired
    private InviteService inviteService;

    @Autowired
    private UserService userService;

    @ApiOperation(value = "关系绑定")
    @PreAuthorize("hasAnyRole('MANAGER')")
    @RequestMapping(value = "/bind", method = RequestMethod.POST)
    ApiResp<Integer> inviteBind(@RequestParam(value = "inviteCode") String inviteCode,
                                @RequestParam(value = "digitId") String digitId) {
        User user = userService.getUserByDigitId(digitId);
        int ret = inviteService.inviteFromDigitId(user, inviteCode);
        return ApiResp.succeed(ret);
    }

    @ApiOperation(value = "获取邀请码用户信息")
    @PreAuthorize("hasAnyRole('MANAGER')")
    @RequestMapping(value = "/bind", method = RequestMethod.GET)
    ApiResp<User> getInviteInfo(@RequestParam(value = "inviteCode") String inviteCode) {

        User user = inviteService.getInviteUserFromDigitId(inviteCode);
        return ApiResp.succeed(user);
    }
}
