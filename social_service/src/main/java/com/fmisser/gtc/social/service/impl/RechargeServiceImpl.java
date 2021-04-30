package com.fmisser.gtc.social.service.impl;

import com.fmisser.gtc.base.dto.apple.IapReceiptVerifyDto;
import com.fmisser.gtc.base.dto.social.IapReceiptDto;
import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.base.prop.AppleConfProp;
import com.fmisser.gtc.social.domain.*;
import com.fmisser.gtc.social.feign.IapVerifyFeign;
import com.fmisser.gtc.social.repository.AssetRepository;
import com.fmisser.gtc.social.repository.IapReceiptRepository;
import com.fmisser.gtc.social.repository.RechargeRepository;
import com.fmisser.gtc.social.service.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

@Slf4j
@Service
public class RechargeServiceImpl implements RechargeService {

    private final AppleConfProp appleConfProp;

    private final IapVerifyFeign iapVerifyFeign;

    private final IapReceiptRepository iapReceiptRepository;

    private final TransactionService transactionService;

    private final RechargeRepository rechargeRepository;

    private final ProductService productService;

    private final AssetRepository assetRepository;

    private final CouponService couponService;

    private final SysConfigService sysConfigService;

    public RechargeServiceImpl(AppleConfProp appleConfProp,
                               IapVerifyFeign iapVerifyFeign,
                               IapReceiptRepository iapReceiptRepository,
                               TransactionService transactionService,
                               RechargeRepository rechargeRepository,
                               ProductService productService,
                               AssetRepository assetRepository,
                               CouponService couponService,
                               SysConfigService sysConfigService) {
        this.appleConfProp = appleConfProp;
        this.iapVerifyFeign = iapVerifyFeign;
        this.iapReceiptRepository = iapReceiptRepository;
        this.transactionService = transactionService;
        this.rechargeRepository = rechargeRepository;
        this.productService = productService;
        this.assetRepository = assetRepository;
        this.couponService = couponService;
        this.sysConfigService = sysConfigService;
    }

    @Transactional
    @Override
    public String createOrder(User user, String productName) throws ApiException {
        Product product = productService.getProductByName(productName);

        Recharge recharge = new Recharge();
        recharge.setOrderNumber(createRechargeOrderNumber(user.getId()));
        recharge.setUserId(user.getId());
        recharge.setProductId(product.getId());
        recharge.setCoin(product.getCoin());
        recharge.setPrice(product.getPrice());
        recharge.setStatus(1);

        recharge = rechargeRepository.save(recharge);

        return recharge.getOrderNumber();
    }

    @Transactional
    @Override
    public String updateOrder(User user, String orderNumber, int status) throws ApiException {
        Recharge recharge = rechargeRepository.findByOrderNumber(orderNumber);
        if (!recharge.getUserId().equals(user.getId())) {
            throw new ApiException(-1, "非法操作");
        }

        recharge.setStatus(status);

        recharge = rechargeRepository.save(recharge);

        return recharge.getOrderNumber();
    }

    @Retryable
    @Transactional
    @Override
    public String completeIapOrder(User user, String orderNumber, int env, String receipt,
                                   String iapProductId, String transactionId, Date purchaseDate) throws ApiException {

        log.info("[info][Recharge Service] request complete iap order, user: {} orderNumber: {} and iapProductId: {} and transactionId: {}" +
                " and receipt: {}",  user.getDigitId(), orderNumber, iapProductId, transactionId, receipt);

        Recharge recharge;

        // 检测票据是否已完成交易
        IapReceipt iapReceipt = iapReceiptRepository.findByTransactionId(transactionId);
        if (Objects.nonNull(iapReceipt)) {

            Optional<Recharge> rechargeOptional = rechargeRepository.findById(iapReceipt.getRechargeId());
            if (!rechargeOptional.isPresent()) {
                log.error("[error][Recharge Service] no order info , user: {} orderNumber: {} and iapProductId: {} and transactionId: {}" +
                        " and receipt: {} ", user.getDigitId(), orderNumber, iapProductId, transactionId, receipt);
                throw new ApiException(-1, "无订单信息");
            }

            recharge = rechargeOptional.get();

            if (!recharge.getUserId().equals(user.getId())) {
                log.error("[error][Recharge Service] user info not match , user: {} orderNumber: {} and iapProductId: {} and transactionId: {}" +
                        " and receipt: {} ", user.getDigitId(), orderNumber, iapProductId, transactionId, receipt);
                throw new ApiException(-1, "非法操作");
            }

            if (recharge.getStatus() != 12) {
                log.error("[error][Recharge Service] order status can't operate , user: {} orderNumber: {} and iapProductId: {} and transactionId: {}" +
                        " and receipt: {} ", user.getDigitId(), orderNumber, iapProductId, transactionId, receipt);
                throw new ApiException(-1, "订单状态不可操作");
            }

            // 校验用户
            if (!user.getId().equals(iapReceipt.getUserId())) {
                log.error("[error][Recharge Service] user info not match 2, user: {} orderNumber: {} and iapProductId: {} and transactionId: {}" +
                        " and receipt: {} ", user.getDigitId(), orderNumber, iapProductId, transactionId, receipt);
                throw new ApiException(-1, "非法操作");
            }

            // 检验票据状态
            if (iapReceipt.getStatus() == 1) {
                log.error("[error][Recharge Service] order status complete , user: {} orderNumber: {} and iapProductId: {} and transactionId: {}" +
                        " and receipt: {} ", user.getDigitId(), orderNumber, iapProductId, transactionId, receipt);
                throw new ApiException(-1, "该交易已完成!");
            }
        } else {
            if (Objects.isNull(orderNumber)) {
                log.error("[error][Recharge Service] no order info , user: {} orderNumber: {} and iapProductId: {} and transactionId: {}" +
                        " and receipt: {} ", user.getDigitId(), orderNumber, iapProductId, transactionId, receipt);
                throw new ApiException(-1, "无订单信息");
            }

            recharge = rechargeRepository.findByOrderNumber(orderNumber);
            if (!recharge.getUserId().equals(user.getId())) {
                log.error("[error][Recharge Service] user info not match , user: {} orderNumber: {} and iapProductId: {} and transactionId: {}" +
                        " and receipt: {} ", user.getDigitId(), orderNumber, iapProductId, transactionId, receipt);
                throw new ApiException(-1, "非法操作");
            }

            if (recharge.getStatus() != 12) {
                log.error("[error][Recharge Service] order status can't operate 2, user: {} orderNumber: {} and iapProductId: {} and transactionId: {}" +
                        " and receipt: {} ", user.getDigitId(), orderNumber, iapProductId, transactionId, receipt);
                throw new ApiException(-1, "订单状态不可操作");
            }

            iapReceipt = new IapReceipt();
            iapReceipt.setRechargeId(recharge.getId());
            iapReceipt.setUserId(user.getId());
            iapReceipt.setReceipt(receipt);
            iapReceipt.setEnv(env);
            iapReceipt.setProductId(iapProductId);
            iapReceipt.setTransactionId(transactionId);
            iapReceipt.setPurchaseDate(purchaseDate);
        }

        // 校验产品是否一致
        Product product = productService.getProductByName(iapProductId);
        if (Objects.isNull(product) || !product.getId().equals(recharge.getProductId())) {
            log.error("[error][Recharge Service] product not match , user: {} orderNumber: {} and iapProductId: {} and transactionId: {}" +
                    " and receipt: {} ", user.getDigitId(), orderNumber, iapProductId, transactionId, receipt);
            throw new ApiException(-1, "非法操作");
        }

        String url = env == 1 ?
                appleConfProp.getProdVerifyUrl() : appleConfProp.getSandBoxVerifyUrl();
        IapReceiptVerifyDto iapReceiptVerifyDto = new IapReceiptVerifyDto(receipt);
        // 去苹果服务器校验
        IapReceiptDto iapReceiptDto = null;
        try {
            iapReceiptDto = iapVerifyFeign.verifyReceipt(new URI(url), iapReceiptVerifyDto);
        } catch (Exception e) {
            log.error("[error][Recharge Service] receipt verify request failed , user: {} orderNumber: {} and iapProductId: {} and transactionId: {}" +
                    " and receipt: {} ", user.getDigitId(), orderNumber, iapProductId, transactionId, receipt);
            throw new ApiException(-1, "票据校验请求失败");
        }

        // 处理用户充值逻辑
        if (iapReceiptDto.getStatus() != 0) {
            log.error("[error][Recharge Service] receipt verify failed , user: {} orderNumber: {} and iapProductId: {} and transactionId: {}" +
                    " and receipt: {} ", user.getDigitId(), orderNumber, iapProductId, transactionId, receipt);
            throw new ApiException(-1,"订单票据校验失败");
        }

        // 检验票据和交易号，查找是否有一个匹配的充值选项
        boolean valid = false;
        for (IapReceiptDto.InApp inApp : iapReceiptDto.getReceipt().getIn_app()) {

            if (inApp.getQuantity() == 1 &&
                    inApp.getProduct_id().equals(iapProductId) &&
                    inApp.getTransaction_id().equals(transactionId)) {
                valid = true;
                break;
            }
        }

        if (!valid) {
            log.error("[error][Recharge Service] no valid recharge info , user: {} orderNumber: {} and iapProductId: {} and transactionId: {}" +
                    " and receipt: {} ", user.getDigitId(), orderNumber, iapProductId, transactionId, receipt);
            throw new ApiException(-1, "充值数据异常");
        }

        // 加钱
        BigDecimal addCoin = recharge.getCoin();
        int ret = assetRepository.
                addCoin(user.getId(), addCoin);
        if (ret != 1) {
            log.error("[error][Recharge Service] update user assets failed , user: {} orderNumber: {} and iapProductId: {} and transactionId: {}" +
                    " and receipt: {} ", user.getDigitId(), orderNumber, iapProductId, transactionId, receipt);
            throw new ApiException(-1, "更新用户金币出错!");
        }
        Asset asset = assetRepository.findByUserId(iapReceipt.getUserId());

        // 更新充值信息
        recharge.setPay(product.getPrice());
        // 苹果抽成数值在改变，所以这里默认不做抽成计算，全额统计
        recharge.setIncome(product.getPrice());
        recharge.setCoinAfter(asset.getCoin());
        recharge.setCoinBefore(asset.getCoin().subtract(addCoin));
        recharge.setStatus(20);
        recharge.setType(0);
        recharge.setRemark("iap recharge");
        recharge.setFinishTime(new Date());
        rechargeRepository.save(recharge);

        // 票据设置完成交易
        iapReceipt.setStatus(1);
        iapReceiptRepository.save(iapReceipt);

        // 判断是否是首次充值，首次充值送聊天券和视频卡
        // 考虑以后退款等操作，都认为是已完成过充值，不再赠送
        // TODO: 2021/4/21 异步去做 丢到消息队列
        Long count = rechargeRepository.countByUserIdAndStatusGreaterThanEqual(iapReceipt.getUserId(), 20);
        if (count == 1) {
            // 首充
            if (sysConfigService.isFirstRechargeFreeMsg()) {
                couponService.addCommMsgFreeCoupon(iapReceipt.getUserId(), 100, 20);
            }

            if (sysConfigService.isFirstRechargeFreeVideo()) {
                couponService.addCommVideoCoupon(iapReceipt.getUserId(), 1, 20);
            }
        }

        return "success";
    }

    //    @Transactional
//    @Retryable
    @Override
    @SneakyThrows
    public String IapVerifyReceipt(User user,
                                   String receipt, int env,
                                   String productId, String transactionId,
                                   Date purchaseDate) throws ApiException {

        // 判断是否已经完成了交易
        IapReceipt iapReceipt = iapReceiptRepository.findByTransactionId(transactionId);
        if (iapReceipt != null) {
            // 这里只校验用户是不是一样，其他数据不作判断，根据初次提交的数据来做逻辑处理
            if (!user.getId().equals(iapReceipt.getUserId())) {
                throw new ApiException(-1, "非法操作!");
            }

            // FIXME: 2020/12/17 考虑一个并发或者重复提交问题，如果WORK1已经进入transactionService.iapPaySuccess(transactionId)，但事务还没有提交，
            //  这时候WORK2执行到此处，此时这里的判断无效，如果后面不做任何处理，transactionService.iapPaySuccess(transactionId) 会再执行一次加金币操作，
            //  这会导致如果是重复请求，同一个业务执行了多次，数据上出错了，
            //  所以这里只做常规判断，不考虑并发或者重复提交问题，这个问题交由transactionService.iapPaySuccess(transactionId)去处理
            if (iapReceipt.getStatus() == 1) {
                throw new ApiException(-1, "该交易已完成!");
            }
        } else {
            iapReceipt = new IapReceipt();
            // 保存用户请求数据
            iapReceipt.setUserId(user.getId());
            iapReceipt.setReceipt(receipt);
            iapReceipt.setEnv(env == 1 ? 1 : 0);
            iapReceipt.setProductId(productId);
            iapReceipt.setTransactionId(transactionId);
            iapReceipt.setPurchaseDate(purchaseDate);
            iapReceipt = iapReceiptRepository.save(iapReceipt);
        }

        String url = iapReceipt.getEnv() == 1 ?
                appleConfProp.getProdVerifyUrl() : appleConfProp.getSandBoxVerifyUrl();
        IapReceiptVerifyDto iapReceiptVerifyDto = new IapReceiptVerifyDto(iapReceipt.getReceipt());
        // 去苹果服务器校验
        IapReceiptDto iapReceiptDto = iapVerifyFeign.verifyReceipt(
                new URI(url),
                iapReceiptVerifyDto);

        // 处理用户充值逻辑
        if (iapReceiptDto.getStatus() == 0) {
            // TODO: 2020/12/16 校验前端提供的支付信息和苹果后端解析的票据
//            if (iapReceiptDto.getReceipt().getIn_app().size() != 1) {
//                // 多条数据认为是异常
//                throw new ApiException(-1, "充值数据异常");
//            }

//            IapReceiptDto.InApp inApp = iapReceiptDto.getReceipt().getIn_app().get(0);
//            // 购买的数量不止一个,交易号和商品不匹配都认为是异常
//            if (inApp.getQuantity() != 1 ||
//                    !inApp.getProduct_id().equals(iapReceipt.getProductId()) ||
//                    !inApp.getTransaction_id().equals(iapReceipt.getTransactionId())) {
//                throw new ApiException(-1, "充值数据异常");
//            }

            // 查找是否有一个匹配的充值选项
            boolean valid = false;
            for (IapReceiptDto.InApp inApp : iapReceiptDto.getReceipt().getIn_app()) {
                if (inApp.getQuantity() == 1 &&
                        inApp.getProduct_id().equals(iapReceipt.getProductId()) &&
                        inApp.getTransaction_id().equals(iapReceipt.getTransactionId())) {
                    valid = true;
                    break;
                }
            }

            if (!valid) {
                throw new ApiException(-1, "充值数据异常");
            }

            // 成功 此时认为apple已收到用户付款
            // 此处开辟新的事务，即使出错 也不影响 iap receipt 数据存储
            return transactionService.iapPaySuccess(transactionId);
        } else {
            throw new ApiException(-1, "充值异常");
        }
    }

    @Override
    public Long getUserRechargeCount(User user) throws ApiException {
        return rechargeRepository.countByUserIdAndStatusGreaterThanEqual(user.getId(), 20);
    }

    @Retryable
    @Transactional
    @Override
    public int completePayOrder(User user, User inviteUser, String orderNumber, Long productId, BigDecimal coin,
                                    BigDecimal price, BigDecimal pay) throws ApiException {

        Asset asset = assetRepository.findByUserId(user.getId());

        Recharge recharge = new Recharge();
        recharge.setOrderNumber(orderNumber);
        recharge.setUserId(user.getId());
        recharge.setProductId(productId);
        recharge.setCoin(coin);
        recharge.setPrice(price);
        recharge.setPay(pay);
        recharge.setIncome(pay);
        recharge.setCoinBefore(asset.getCoin());
        recharge.setCoinAfter(asset.getCoin().add(coin));
        recharge.setStatus(20);
        recharge.setType(11);

        // 如果邀请人不为空
        if (Objects.nonNull(inviteUser)) {
            Asset inviteUserAsset = assetRepository.findByUserId(inviteUser.getId());

            BigDecimal rewardCoin = coin.multiply(BigDecimal.valueOf(0.02));

            Recharge rewardRecharge = new Recharge();
            rewardRecharge.setOrderNumber(createRechargeOrderNumber(inviteUser.getId()));
            rewardRecharge.setUserId(inviteUser.getId());
            rewardRecharge.setProductId(0L);
            rewardRecharge.setCoin(coin.multiply(BigDecimal.valueOf(0.02)));
            rewardRecharge.setPrice(BigDecimal.ZERO);
            rewardRecharge.setPay(BigDecimal.ZERO);
            rewardRecharge.setIncome(BigDecimal.ZERO);
            rewardRecharge.setCoinBefore(inviteUserAsset.getCoin());
            rewardRecharge.setCoinAfter(inviteUserAsset.getCoin().add(rewardCoin));
            rewardRecharge.setStatus(20);
            rewardRecharge.setType(12);
            rewardRecharge.setRemark(user.getDigitId());

            assetRepository.addCoin(inviteUser.getId(), rewardCoin);

            rechargeRepository.save(rewardRecharge);

            // 上分保存邀请人的信息
            recharge.setRemark(inviteUser.getDigitId());
        }

        assetRepository.addCoin(user.getId(), coin);

        rechargeRepository.save(recharge);

        return 1;
    }

    // 创建支付订单号
    public static String createRechargeOrderNumber(Long userId) {
        // 订单号 = 当前时间戳 + 用户id（格式化成10位） + 随机4位数
        return String.format("%d%010d%04d",
                new Date().getTime(),
                userId,
                new Random().nextInt(9999));
    }
}
