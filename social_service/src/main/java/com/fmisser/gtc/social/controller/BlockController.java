package com.fmisser.gtc.social.controller;


import com.fmisser.gtc.base.response.ApiResp;
import com.fmisser.gtc.social.domain.User;
import com.fmisser.gtc.social.service.BlockService;
import com.fmisser.gtc.social.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.hibernate.validator.constraints.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(description = "屏蔽API")
@RestController
@RequestMapping("/block")
@Validated
public class BlockController {

    private final BlockService blockService;

    private final UserService userService;

    public BlockController(BlockService blockService,
                           UserService userService) {
        this.blockService = blockService;
        this.userService = userService;
    }

    @ApiOperation("屏蔽某一条动态")
    @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "String", paramType = "header")
    @PostMapping(value = "/pick-one-dynamic")
    public ApiResp<Integer> blockOnDynamic(@RequestParam("dstUserDigitId") String dstUserDigitId,
                                     @RequestParam("dstDynamicId") Long dstDynamicId,
                                     @RequestParam("block") @Range(min = 0, max = 1) int block) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getPrincipal().toString();
        User userDo = userService.getUserByUsername(username);

        User dstUserDo = userService.getUserByDigitId(dstUserDigitId);

        int ret = blockService.blockOneDynamic(userDo, dstUserDo, dstDynamicId, block);
        return ApiResp.succeed(ret);
    }

    @ApiOperation("屏蔽某一用户所有动态")
    @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "String", paramType = "header")
    @PostMapping(value = "/pick-all-dynamic")
    public ApiResp<Integer> blockAllDynamic(@RequestParam("dstUserDigitId") String dstUserDigitId,
                                     @RequestParam("block") @Range(min = 0, max = 1) int block) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getPrincipal().toString();
        User userDo = userService.getUserByUsername(username);

        User dstUserDo = userService.getUserByDigitId(dstUserDigitId);

        int ret = blockService.blockUserDynamic(userDo, dstUserDo, block);
        return ApiResp.succeed(ret);
    }

    @ApiOperation("拉黑某一个用户")
    @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "String", paramType = "header")
    @PostMapping(value = "/pick-one-user")
    public ApiResp<Integer> blockOneUser(@RequestParam("dstUserDigitId") String dstUserDigitId,
                                            @RequestParam("block") @Range(min = 0, max = 1) int block) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getPrincipal().toString();
        User userDo = userService.getUserByUsername(username);

        User dstUserDo = userService.getUserByDigitId(dstUserDigitId);

        int ret = blockService.blockUser(userDo, dstUserDo, block);
        return ApiResp.succeed(ret);
    }
}
