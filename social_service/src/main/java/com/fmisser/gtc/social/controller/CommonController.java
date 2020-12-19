package com.fmisser.gtc.social.controller;

import com.fmisser.gtc.base.response.ApiResp;
import com.fmisser.gtc.social.domain.Product;
import com.fmisser.gtc.social.domain.User;
import com.fmisser.gtc.social.service.ProductService;
import com.fmisser.gtc.social.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@Api(description = "通用API")
@RestController
@RequestMapping("/comm")
@Validated
public class CommonController {
    private final UserService userService;
    private final ProductService productService;

    public CommonController(UserService userService,
                            ProductService productService) {
        this.userService = userService;
        this.productService = productService;
    }

    @ApiOperation(value = "获取主播列表")
    @GetMapping(value = "/list-anchor")
    ApiResp<List<User>> getAnchorList(@RequestParam(value = "type") Integer type,
                                      @RequestParam(value = "gender", required = false) Integer gender,
                                      @RequestParam(value = "pageIndex", required = false, defaultValue = "0") int pageIndex,
                                      @RequestParam(value = "pageSize", required = false, defaultValue = "30") int pageSize) {
        List<User> userList = userService.getAnchorList(type, gender, pageIndex, pageSize);
        return ApiResp.succeed(userList);
    }

    @ApiOperation(value = "获取主播详情")
    @GetMapping(value = "/anchor-profile")
    ApiResp<User> getAnchorInfo(@RequestParam("digitId") String digitId) {
        User user = userService.getUserByDigitId(digitId);
        return ApiResp.succeed(userService.profile(user));
    }

    @ApiOperation(value = "获取苹果支付商品列表")
    @GetMapping(value = "/list-iap-products")
    public ApiResp<List<Product>> getIapProducts() {
        List<Product> products = this.productService.getIapProductList();
        return ApiResp.succeed(products);
    }

    // https://help.apple.com/app-store-connect/#/dev0067a330b
    // 接收app store服务器通知
    @PostMapping(value = "/apple-stsn")
    public ApiResp<String> receiveAppleStsn() {
        return ApiResp.succeed("ok!");
    }

    @ApiOperation(value = "判断该手机用户是否已存在")
    @GetMapping(value = "/is-reg")
    ApiResp<Integer> isPhoneReg(@RequestParam("phone") String phone) {
        User userDo = userService.getUserByUsername(phone);
        if (Objects.nonNull(userDo)) {
            return ApiResp.succeed(1);
        } else {
            return ApiResp.succeed(0);
        }
    }
}
