package com.fmisser.gtc.social.service.impl;

import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.base.prop.AppleConfProp;
import com.fmisser.gtc.social.domain.*;
import com.fmisser.gtc.social.feign.IapVerifyFeign;
import com.fmisser.gtc.social.repository.AssetRepository;
import com.fmisser.gtc.social.repository.IapReceiptRepository;
import com.fmisser.gtc.social.repository.ProductRepository;
import com.fmisser.gtc.social.repository.RechargeRepository;
import com.fmisser.gtc.social.service.TransactionService;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Optional;
import java.util.Random;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final AssetRepository assetRepository;

    private final ProductRepository productRepository;

    private final RechargeRepository rechargeRepository;

    private final IapReceiptRepository iapReceiptRepository;

    public TransactionServiceImpl(AssetRepository assetRepository,
                                  ProductRepository productRepository,
                                  RechargeRepository rechargeRepository,
                                  IapReceiptRepository iapReceiptRepository) {
        this.assetRepository = assetRepository;
        this.productRepository = productRepository;
        this.rechargeRepository = rechargeRepository;
        this.iapReceiptRepository = iapReceiptRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
//    @Retryable
    @Override
    public String iapPaySuccess(String transactionId) throws ApiException {

        // FIXME: 2020/12/17 接上一步考虑的重复或者并发问题引起的数据问题，此处通过使用当前读加锁机制来防止并发问题，
        //  直到先获取到资源的WORK提交了事务或者出错回滚，其他WORK才能继续，通过再此处判断状态是否改变，可有效避免并发和重复提交问题
        //  transactionId 是唯一索引，RC,RR级别下只会对当前唯一索引和主键以及当前数据加锁 ，慎用锁，使用不当会导致死锁
        IapReceipt iapReceipt = iapReceiptRepository.getByTransactionIdWithLock(transactionId);
        if (iapReceipt == null || iapReceipt.getStatus() == 1) {
            throw new ApiException(-1, "交易不存在或已完成!");
        }

        String productId = iapReceipt.getProductId();
//        int quantity = iapReceipt.getQuantity();

//        Product product = productRepository.findByName(productId);
        // 判断产品是否有效
        Optional<Product> productOptional = productRepository.getValidProduct(productId);
        if (!productOptional.isPresent()) {
            throw new ApiException(-1, "产品无效或已过期！");
        }

        Product product = productOptional.get();
        // 不是iap类型的支付
        if (product.getPlt() != 0) {
            throw new ApiException(-1, "不是有效的购买商品");
        }

        // step1: 给用户加金币, 请勿使用以下方法，
        // 同样是高并发下如果在获取金币后和加金币之间可能其他地方加减了金币
        // 这样计算出来的金币不正确
//        Asset asset = assetRepository.findByUserId(iapReceipt.getUserId());
//        BigDecimal beforeCoin = asset.getCoin();
//        // 购买一个获得的币乘以购买的数量
//        BigDecimal afterCoin = beforeCoin.add(product.getCoin().multiply(BigDecimal.valueOf(quantity)));
//        asset.setCoin(afterCoin);
//        assetRepository.save(asset);

        // step1： 给用户加金币
//        BigDecimal addCoin = product.getCoin().multiply(BigDecimal.valueOf(quantity));
        BigDecimal addCoin = product.getCoin();
        int ret = assetRepository.
                addCoin(iapReceipt.getUserId(), addCoin);
        if (ret != 1) {
            throw new ApiException(-1, "更新用户金币出错!");
        }
        Asset asset = assetRepository.findByUserId(iapReceipt.getUserId());

        // step2：记录充值
        Recharge recharge = new Recharge();
        recharge.setOrderNumber(createRechargeOrderNumber(iapReceipt.getUserId()));
        recharge.setUserId(iapReceipt.getUserId());
        // 注意： 这里的product id是 Product的主键
        recharge.setProductId(product.getId());
        recharge.setCoin(product.getCoin());
        recharge.setPrice(product.getPrice());
        // 苹果支付付款默认为是商品价格乘以数量
//        BigDecimal totalPrice = product.getPrice().multiply(BigDecimal.valueOf(quantity));
        BigDecimal totalPrice = product.getPrice();
        recharge.setPay(totalPrice);
        // 苹果抽成数值在改变，所以这里默认不做抽成计算，全额统计
        recharge.setIncome(totalPrice);
        recharge.setCoinAfter(asset.getCoin());
        // 减去充值的币
        recharge.setCoinBefore(asset.getCoin().subtract(addCoin));
        // 认为充值完成
        recharge.setStatus(20);
        // iap
        recharge.setType(0);
        recharge.setRemark("iap recharge");
        recharge.setFinishTime(new Date());
        recharge = rechargeRepository.save(recharge);

        // step3  处理成功，iap receipt 关联 recharge id，设置完成交易
        iapReceipt.setRechargeId(recharge.getId());
        iapReceipt.setStatus(1);
        iapReceiptRepository.save(iapReceipt);

        return "success";
    }

    // 创建支付订单号
    public static String createRechargeOrderNumber(Long userId) {
        // 订单号 = 当前时间戳 + 用户id（格式化成10位） + 随机4位数
        return String.format("%d%010d%4d",
                new Date().getTime(),
                userId,
                new Random().nextInt(9999));
    }
}
