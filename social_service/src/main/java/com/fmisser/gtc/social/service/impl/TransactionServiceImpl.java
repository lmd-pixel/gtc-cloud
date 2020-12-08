package com.fmisser.gtc.social.service.impl;

import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.base.prop.AppleConfProp;
import com.fmisser.gtc.social.domain.Asset;
import com.fmisser.gtc.social.domain.IapReceipt;
import com.fmisser.gtc.social.domain.Product;
import com.fmisser.gtc.social.domain.Recharge;
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
    @Retryable
    @Override
    public String iapPaySuccess(IapReceipt iapReceipt) throws ApiException {

        String productId = iapReceipt.getProductId();
        int quantity = iapReceipt.getQuantity();
        Product product = productRepository.findByName(productId);

        // step1: 给用户加金币, 请勿使用以下方法，
        // 在获取金币后和加金币之间可能其他地方加减了金币
        // 这样计算出来的金币不正确
//        Asset asset = assetRepository.findByUserId(iapReceipt.getUserId());
//        BigDecimal beforeCoin = asset.getCoin();
//        // 购买一个获得的币乘以购买的数量
//        BigDecimal afterCoin = beforeCoin.add(product.getCoin().multiply(BigDecimal.valueOf(quantity)));
//        asset.setCoin(afterCoin);
//        assetRepository.save(asset);

        // step1： 给用户加金币
        BigDecimal addCoin = product.getCoin().multiply(BigDecimal.valueOf(quantity));
        Asset asset = assetRepository.
                addCoin(iapReceipt.getUserId(), addCoin);

        // step2：记录充值
        Recharge recharge = new Recharge();
        recharge.setOrderNumber(createRechargeOrderNumber(iapReceipt.getUserId()));
        recharge.setUserId(iapReceipt.getUserId());
        // 注意： 这里的product id是 Product的主键
        recharge.setProductId(product.getId());
        recharge.setCoin(product.getCoin());
        recharge.setPrice(product.getPrice());
        // 苹果支付付款默认为是商品价格乘以数量
        BigDecimal totalPrice = product.getPrice().multiply(BigDecimal.valueOf(quantity));
        recharge.setPay(totalPrice);
        // 苹果抽成30%，新规这个数值在改变，所以这里默认不做抽成计算，全额统计
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

        // step3  处理成功，iap receipt 关联 recharge id
        iapReceipt.setRechargeId(recharge.getId());
        iapReceiptRepository.save(iapReceipt);

        return "success";
    }

    // 创建支付订单号
    private String createRechargeOrderNumber(Long userId) {
        // 订单号 = 当前时间戳 + 用户id（格式化成10位） + 随机4位数
        return String.format("%d%10d%4d",
                new Date().getTime(),
                userId,
                new Random().nextInt(9999));
    }
}
