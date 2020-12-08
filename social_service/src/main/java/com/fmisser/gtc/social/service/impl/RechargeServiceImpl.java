package com.fmisser.gtc.social.service.impl;

import com.fmisser.gtc.base.dto.social.IapReceiptDto;
import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.base.prop.AppleConfProp;
import com.fmisser.gtc.base.utils.CryptoUtils;
import com.fmisser.gtc.social.domain.IapReceipt;
import com.fmisser.gtc.social.feign.IapVerifyFeign;
import com.fmisser.gtc.social.repository.IapReceiptRepository;
import com.fmisser.gtc.social.service.RechargeService;
import com.fmisser.gtc.social.service.TransactionService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.util.Objects;

@Service
public class RechargeServiceImpl implements RechargeService {

    private final AppleConfProp appleConfProp;

    private final IapVerifyFeign iapVerifyFeign;

    private final IapReceiptRepository iapReceiptRepository;

    private final TransactionService transactionService;

    public RechargeServiceImpl(AppleConfProp appleConfProp,
                               IapVerifyFeign iapVerifyFeign,
                               IapReceiptRepository iapReceiptRepository,
                               TransactionService transactionService) {
        this.appleConfProp = appleConfProp;
        this.iapVerifyFeign = iapVerifyFeign;
        this.iapReceiptRepository = iapReceiptRepository;
        this.transactionService = transactionService;
    }

    @Transactional
    @Retryable
    @Override
    @SneakyThrows
    public String IapVerifyReceipt(String userId, String receipt, boolean chooseEnv) throws ApiException {
        // TODO: 2020/12/7 验证用户和receipt

        String url = chooseEnv ? appleConfProp.getProdVerifyUrl() : appleConfProp.getSandBoxVerifyUrl();
        String encodeReceipt = CryptoUtils.base64Encode(receipt.getBytes());

        IapReceiptDto iapReceiptDto = iapVerifyFeign.verifyReceipt(new URI(url), encodeReceipt);

        IapReceipt iapReceipt = new IapReceipt();
        iapReceipt.setUserId(Long.parseLong(userId));
        iapReceipt.setEnv(chooseEnv ? 1 : 0);
        iapReceipt.setStatus(iapReceiptDto.getStatus());
        if (Objects.nonNull(iapReceiptDto.getReceipt())) {
            iapReceipt.setQuantity(iapReceiptDto.getReceipt().getQuantity());
            iapReceipt.setProductId(iapReceiptDto.getReceipt().getProduct_id());
            iapReceipt.setTransactionId(iapReceiptDto.getReceipt().getTransaction_id());
            iapReceipt.setPurchaseDate(iapReceiptDto.getReceipt().getPurchase_date());
            iapReceipt.setOriginalTransactionId(iapReceiptDto.getReceipt().getOriginal_transaction_id());
            iapReceipt.setOriginalPurchaseDate(iapReceiptDto.getReceipt().getOriginal_purchase_date());
            iapReceipt.setAppItemId(iapReceiptDto.getReceipt().getApp_item_id());
            iapReceipt.setVersionExternalIdentifier(iapReceiptDto.getReceipt().getVersion_external_identifier());
            iapReceipt.setBid(iapReceiptDto.getReceipt().getBid());
            iapReceipt.setBvrs(iapReceiptDto.getReceipt().getBvrs());
        }

        iapReceipt = iapReceiptRepository.save(iapReceipt);

        // 处理用户充值逻辑
        if (iapReceiptDto.getStatus() == 0) {
            // 成功 此时认为apple已收到用户付款
            // 此处开辟新的事务，即使出错 也不影响 iap receipt 数据存储
            return transactionService.iapPaySuccess(iapReceipt);
        } else {
            throw new ApiException(-1, "充值异常");
        }
    }
}
