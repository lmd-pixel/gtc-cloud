package com.fmisser.gtc.social.service.impl;

import com.fmisser.gtc.base.dto.apple.IapReceiptVerifyDto;
import com.fmisser.gtc.base.dto.social.IapReceiptDto;
import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.base.prop.AppleConfProp;
import com.fmisser.gtc.base.utils.CryptoUtils;
import com.fmisser.gtc.social.domain.IapReceipt;
import com.fmisser.gtc.social.domain.User;
import com.fmisser.gtc.social.feign.IapVerifyFeign;
import com.fmisser.gtc.social.repository.IapReceiptRepository;
import com.fmisser.gtc.social.repository.RechargeRepository;
import com.fmisser.gtc.social.service.RechargeService;
import com.fmisser.gtc.social.service.TransactionService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class RechargeServiceImpl implements RechargeService {

    private final AppleConfProp appleConfProp;

    private final IapVerifyFeign iapVerifyFeign;

    private final IapReceiptRepository iapReceiptRepository;

    private final TransactionService transactionService;

    private final RechargeRepository rechargeRepository;

    public RechargeServiceImpl(AppleConfProp appleConfProp,
                               IapVerifyFeign iapVerifyFeign,
                               IapReceiptRepository iapReceiptRepository,
                               TransactionService transactionService,
                               RechargeRepository rechargeRepository) {
        this.appleConfProp = appleConfProp;
        this.iapVerifyFeign = iapVerifyFeign;
        this.iapReceiptRepository = iapReceiptRepository;
        this.transactionService = transactionService;
        this.rechargeRepository = rechargeRepository;
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
}
