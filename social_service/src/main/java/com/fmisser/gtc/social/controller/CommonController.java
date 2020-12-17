package com.fmisser.gtc.social.controller;

import com.fmisser.gtc.base.response.ApiResp;
import com.fmisser.gtc.social.domain.Product;
import com.fmisser.gtc.social.domain.User;
import com.fmisser.gtc.social.service.ProductService;
import com.fmisser.gtc.social.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    @PostMapping(value = "/list-anchor")
    ApiResp<List<User>> getAnchorList(@RequestParam(value = "type") int type,
                                      @RequestParam(value = "pageIndex", required = false, defaultValue = "0") int pageIndex,
                                      @RequestParam(value = "pageSize", required = false, defaultValue = "30") int pageSize) {
        List<User> userList = userService.getAnchorList(type, pageIndex, pageSize);
        return ApiResp.succeed(userList);
    }

    @ApiOperation(value = "获取苹果支付商品列表")
    @GetMapping(value = "/list-iap-products")
    public ApiResp<List<Product>> getIapProducts() {
        List<Product> products = this.productService.getIapProductList();
        return ApiResp.succeed(products);
    }
}
