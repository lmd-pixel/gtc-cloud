package com.fmisser.gtc.social.controller;

import com.fmisser.gtc.base.response.ApiResp;
import com.fmisser.gtc.social.domain.District;
import com.fmisser.gtc.social.domain.Label;
import com.fmisser.gtc.social.domain.Product;
import com.fmisser.gtc.social.domain.User;
import com.fmisser.gtc.social.service.DistrictService;
import com.fmisser.gtc.social.service.LabelService;
import com.fmisser.gtc.social.service.ProductService;
import com.fmisser.gtc.social.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

@Api(description = "通用API")
@RestController
@RequestMapping("/comm")
@Validated
public class CommonController {
    private final UserService userService;
    private final ProductService productService;
    private final DistrictService districtService;
    private final LabelService labelService;

    public CommonController(UserService userService,
                            ProductService productService,
                            DistrictService districtService,
                            LabelService labelService) {
        this.userService = userService;
        this.productService = productService;
        this.districtService = districtService;
        this.labelService = labelService;
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
    @ApiImplicitParam(name = "Authorization", required = false, dataType = "String", paramType = "header")
    @GetMapping(value = "/anchor-profile")
    ApiResp<User> getAnchorInfo(@RequestParam("digitId") String digitId) {
        User user = userService.getUserByDigitId(digitId);
        User selfUser = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.isAuthenticated()) {
            String username = authentication.getPrincipal().toString();
            // 未授权默认登录的账户名字未 anonymousUser
            if (!username.equals("anonymousUser")) {
                selfUser = userService.getUserByUsername(username);
            }
        }

        return ApiResp.succeed(userService.getAnchorProfile(user, selfUser));
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

    @ApiOperation(value = "获取城市数据")
    @GetMapping(value = "/district")
    public ApiResp<List<District>> getDistrictList() {
        List<District> districtList = districtService.getDistrictList();
        return ApiResp.succeed(districtList);
    }

    @ApiOperation(value = "获取标签数据")
    @GetMapping(value = "/labels")
    public ApiResp<List<Label>> getLabelList() {
        List<Label> labels = labelService.getLabelList();
        return ApiResp.succeed(labels);
    }

    @ApiOperation(value = "获取收费区间")
    @GetMapping(value = "/profit-price")
    public ApiResp<Object> getProfitPrice() {
        Map<String, List<BigDecimal>> resultMap = new HashMap<>();
        List<BigDecimal> msgPrice = Arrays.asList(BigDecimal.ZERO, BigDecimal.ONE);
        List<BigDecimal> callPrice = Arrays.asList(
                BigDecimal.valueOf(100),
                BigDecimal.valueOf(150),
                BigDecimal.valueOf(200),
                BigDecimal.valueOf(250),
                BigDecimal.valueOf(300),
                BigDecimal.valueOf(350),
                BigDecimal.valueOf(400),
                BigDecimal.valueOf(450),
                BigDecimal.valueOf(500)
                );
        resultMap.put("msgPrice", msgPrice);
        resultMap.put("voicePrice", callPrice);
        resultMap.put("videoPrice", callPrice);
        return ApiResp.succeed(resultMap);
    }
}
