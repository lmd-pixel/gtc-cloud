package com.fmisser.gtc.social.controller;

import com.fmisser.gtc.base.dto.social.RecvGiftDto;
import com.fmisser.gtc.base.response.ApiResp;
import com.fmisser.gtc.social.domain.*;
import com.fmisser.gtc.social.mq.WxWebHookBinding;
import com.fmisser.gtc.social.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
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
    private final GiftService giftService;
    private final SysConfigService sysConfigService;

    public CommonController(UserService userService,
                            ProductService productService,
                            DistrictService districtService,
                            LabelService labelService,
                            GiftService giftService,
                            SysConfigService sysConfigService) {
        this.userService = userService;
        this.productService = productService;
        this.districtService = districtService;
        this.labelService = labelService;
        this.giftService = giftService;
        this.sysConfigService = sysConfigService;
    }

    @ApiOperation(value = "获取主播列表")
    @GetMapping(value = "/list-anchor")
    ApiResp<List<User>> getAnchorList(
            @RequestHeader(value = "version", required = false, defaultValue = "v1") String version,
            @RequestParam(value = "type") Integer type,
            @RequestParam(value = "gender", required = false) Integer gender,
            @RequestParam(value = "pageIndex", required = false, defaultValue = "0") int pageIndex,
            @RequestParam(value = "pageSize", required = false, defaultValue = "30") int pageSize) {

        // 针对版本审核
        if (sysConfigService.getAppAuditVersion().equals(version)) {
            List<User> userList = userService.getAuditAnchorList(type, gender, pageIndex, pageSize);
            for (User user:
                 userList) {
                user.setMessagePrice(BigDecimal.valueOf(-1).setScale(2, BigDecimal.ROUND_HALF_UP));
                user.setCallPrice(BigDecimal.valueOf(-1).setScale(2, BigDecimal.ROUND_HALF_UP));
                user.setVideoPrice(BigDecimal.valueOf(-1).setScale(2, BigDecimal.ROUND_HALF_UP));
            }
            return ApiResp.succeed(userList);
        } else {
            List<User> userList = userService.getAnchorList(type, gender, pageIndex, pageSize);
            return ApiResp.succeed(userList);
        }
    }

    @ApiOperation(value = "获取主播详情")
    @ApiImplicitParam(name = "Authorization", required = false, dataType = "String", paramType = "header")
    @GetMapping(value = "/anchor-profile")
    ApiResp<User> getAnchorInfo(
            @RequestHeader(value = "version", required = false, defaultValue = "v1") String version,
            @RequestParam("digitId") String digitId) {
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

        User userDto = userService.getAnchorProfile(user, selfUser);

        // 针对版本审核
        if (sysConfigService.getAppAuditVersion().equals(version)) {
            userDto.setMessagePrice(BigDecimal.valueOf(-1).setScale(2, BigDecimal.ROUND_HALF_UP));
            userDto.setCallPrice(BigDecimal.valueOf(-1).setScale(2, BigDecimal.ROUND_HALF_UP));
            userDto.setVideoPrice(BigDecimal.valueOf(-1).setScale(2, BigDecimal.ROUND_HALF_UP));
        }

        return ApiResp.succeed(userDto);
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

    @ApiOperation(value = "判断当前版本是否是审核版本")
    @GetMapping(value = "/audit-version")
    ApiResp<Integer> getAuditVersion(
            @RequestHeader(value = "version", required = false, defaultValue = "v1") String version) {
        if (sysConfigService.getAppAuditVersion().equals(version)) {
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
//                BigDecimal.valueOf(600),
//                BigDecimal.valueOf(700),
//                BigDecimal.valueOf(800)
                );
        resultMap.put("msgPrice", msgPrice);
        resultMap.put("voicePrice", callPrice);
        resultMap.put("videoPrice", callPrice);
        return ApiResp.succeed(resultMap);
    }

    @ApiOperation(value = "获取职业选择")
    @GetMapping(value = "/profession-list")
    public ApiResp<List<String>> getProfessionList() {
        List<String> professionList =
                Arrays.asList("学生", "上班族", "空姐", "模特", "演员", "歌手", "舞者", "健身教练", "教师", "护士", "自由职业");
        return ApiResp.succeed(professionList);
    }

    @ApiOperation(value = "获取礼物列表")
    @GetMapping(value = "/gift-list")
    public ApiResp<List<Gift>> getGiftList() {
        List<Gift> giftList = giftService.getGiftList();
        return ApiResp.succeed(giftList);
    }

    @ApiOperation("收到的礼物列表")
    @GetMapping("/recv-list")
    public ApiResp<List<RecvGiftDto>> getRecvGiftList(@RequestParam("digitId") String digitId,
                                                      @RequestParam("pageIndex") int pageIndex,
                                                      @RequestParam("pageSize") int pageSize) {
        User userDo = userService.getUserByDigitId(digitId);
        List<RecvGiftDto> recvGiftDtoList = giftService.getRecvGiftList(userDo, pageIndex, pageSize);
        return ApiResp.succeed(recvGiftDtoList);
    }
}
