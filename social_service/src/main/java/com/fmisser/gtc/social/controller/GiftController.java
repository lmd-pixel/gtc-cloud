package com.fmisser.gtc.social.controller;

import com.fmisser.gtc.base.dto.social.RecvGiftDto;
import com.fmisser.gtc.base.response.ApiResp;
import com.fmisser.gtc.social.domain.User;
import com.fmisser.gtc.social.service.GiftService;
import com.fmisser.gtc.social.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(description = "礼物API")
@RestController
@RequestMapping("/gift")
@Validated
public class GiftController {
    private final GiftService giftService;

    private final UserService userService;

    public GiftController(GiftService giftService, UserService userService) {
        this.giftService = giftService;
        this.userService = userService;
    }

    @ApiOperation("赠送礼物")
    @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "String", paramType = "header")
    @PostMapping("/send-gift")
    public ApiResp<Integer> sendGift(@RequestParam("digitId") String digitId,
                                     @RequestParam("giftId") Long giftId,
                                     @RequestParam(value = "count", required = false, defaultValue = "1") Integer count) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getPrincipal().toString();
        User userDo = userService.getUserByUsername(username);

        User userTo = userService.getUserByDigitId(digitId);
        int ret = giftService.postGift(userDo, userTo, giftId, count);
        return ApiResp.succeed(ret);
    }

}
