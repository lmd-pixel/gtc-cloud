package com.fmisser.gtc.social.service.impl;

import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.social.domain.Asset;
import com.fmisser.gtc.social.domain.User;
import com.fmisser.gtc.social.domain.WithdrawAudit;
import com.fmisser.gtc.social.repository.AssetRepository;
import com.fmisser.gtc.social.repository.WithdrawAuditRepository;
import com.fmisser.gtc.social.service.WithdrawService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
public class WithdrawServiceImpl implements WithdrawService {

    @Autowired
    private WithdrawAuditRepository withdrawAuditRepository;

    @Autowired
    private AssetRepository assetRepository;

    @Transactional
    @Override
    public WithdrawAudit requestWithdraw(User user, BigDecimal coin) throws ApiException {
        WithdrawAudit withdrawAudit = getCurrWithdraw(user);
        if (withdrawAudit != null) {
            throw new ApiException(-1, "当前已有提现在进行中，无法发起新的提现");
        }

        Asset asset = assetRepository.findByUserId(user.getId());
        if (asset.getCoin().compareTo(coin) < 0) {
            throw new ApiException(-1, "当前提现金额超过聊币金额，无法发起提现");
        }

        BigDecimal feeRatio = BigDecimal.valueOf(0.03);
        BigDecimal drawCurr = coin;
        BigDecimal drawMax = asset.getCoin();
        BigDecimal coinBefore = asset.getCoin();
        BigDecimal coinAfter = coinBefore.subtract(coin);

        BigDecimal fee = drawCurr.multiply(feeRatio).setScale(2, BigDecimal.ROUND_HALF_UP);
        BigDecimal drawActual = drawCurr.subtract(fee);
        BigDecimal payMoney = drawActual.divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_HALF_UP);

        WithdrawAudit newAudit = new WithdrawAudit();
        newAudit.setUserId(user.getId());
        newAudit.setOrderNumber(createWithdrawOrderNumber(user.getId()));
        newAudit.setDrawCurr(drawCurr);
        newAudit.setDrawMax(drawMax);
        newAudit.setDrawActual(drawActual);
        newAudit.setCoinBefore(coinBefore);
        newAudit.setCoinAfter(coinAfter);
        newAudit.setFee(fee);
        newAudit.setFeeRatio(feeRatio);
        newAudit.setPayMoney(payMoney);
        newAudit.setPayActual(payMoney);
        newAudit.setPayType(0);
        newAudit.setPayToPeople(asset.getAlipayName());
        newAudit.setPayToAccount(asset.getAlipayNumber());
        newAudit.setPayToPhone(asset.getPhone());
        newAudit.setStatus(10);
        newAudit = withdrawAuditRepository.save(newAudit);

        // 提现减聊币
        assetRepository.subCoin(user.getId(), coin);

        return _prepareResponse(newAudit);
    }

    @Override
    public WithdrawAudit getCurrWithdraw(User user) throws ApiException {
        // 审核中，审核完成
        List<Integer> statusList = Arrays.asList(10, 30);
        WithdrawAudit withdrawAudit = withdrawAuditRepository.findTopByUserIdAndStatusIn(user.getId(), statusList);
//        WithdrawAudit withdrawAudit = withdrawAuditRepository.findByUserIdAndStatusLessThan(user.getId(), 20);
        if (Objects.nonNull(withdrawAudit)) {
            return _prepareResponse(withdrawAudit);
        } else {
            return null;
        }
    }

    protected WithdrawAudit _prepareResponse(WithdrawAudit withdrawAudit) {
        withdrawAudit.setDrawCurr(withdrawAudit.getDrawCurr().setScale(2, BigDecimal.ROUND_HALF_UP));
        return withdrawAudit;
    }

    // 创建支付订单号
    public static String createWithdrawOrderNumber(Long userId) {
        // 订单号 = 当前时间戳 + 用户id（格式化成10位） + 随机4位数
        return String.format("%d%010d%4d",
                new Date().getTime(),
                userId,
                new Random().nextInt(9999));
    }
}
