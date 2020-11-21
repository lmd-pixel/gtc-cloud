package com.fmisser.gtc.social.service.impl;

import com.fmisser.gtc.base.dto.im.ImAfterSendMsgDto;
import com.fmisser.gtc.base.dto.im.ImBeforeSendMsgDto;
import com.fmisser.gtc.base.dto.im.ImCbResp;
import com.fmisser.gtc.base.dto.im.ImStateChangeDto;
import com.fmisser.gtc.social.domain.Asset;
import com.fmisser.gtc.social.domain.MessageBill;
import com.fmisser.gtc.social.domain.User;
import com.fmisser.gtc.social.repository.AssetRepository;
import com.fmisser.gtc.social.repository.MessageBillRepository;
import com.fmisser.gtc.social.repository.UserRepository;
import com.fmisser.gtc.social.service.ImCallbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class ImCallbackServiceImpl implements ImCallbackService {

    @Autowired
    private AssetRepository assetRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MessageBillRepository messageBillRepository;

    @Override
    public ImCbResp stateChangeCallback(ImStateChangeDto imStateChangeDto) {
        return null;
    }

    @Override
    public ImCbResp beforeSendMsg(ImBeforeSendMsgDto imBeforeSendMsgDto) {
        ImCbResp resp = new ImCbResp();
        resp.setActionStatus("OK");

        // TODO: 2020/11/20 记录数据

        Long userIdFrom = Long.parseLong(imBeforeSendMsgDto.getFrom_Account());
        Long userIdTo = Long.parseLong(imBeforeSendMsgDto.getTo_Account());

        Optional<User> userTo = userRepository.findById(userIdTo);
        if (userTo.get().getIdentity() == 0) {
            // 目标用户是普通用户，则不用考虑金币问题
            return resp;
        }

        BigDecimal messagePrice = userTo.get().getMessagePrice();
        Asset assetFrom = assetRepository.findByUserId(userIdFrom);
        BigDecimal coinFrom = assetFrom.getCoin();

        if (coinFrom.compareTo(messagePrice) < 0) {
            // 金币不够
            resp.setErrorCode(121001);
            resp.setErrorInfo("金币不够了，快去充值吧");
        }

        return resp;
    }

    @Transactional
    @Retryable
    @Override
    public ImCbResp afterSendMsg(ImAfterSendMsgDto imAfterSendMsgDto) {
        ImCbResp resp = new ImCbResp();
        resp.setActionStatus("OK");

        // TODO: 2020/11/20 记录数据

        if (imAfterSendMsgDto.getSendMsgResult() != 0) {
            // 消息发送失败了，记录原因
            return resp;
        }

        Long userIdTo = Long.parseLong(imAfterSendMsgDto.getTo_Account());
        Long userIdFrom = Long.parseLong(imAfterSendMsgDto.getFrom_Account());

        Optional<User> userTo = userRepository.findById(userIdTo);
        if (userTo.get().getIdentity() == 0) {
            // 目标身份是普通用户，则不用计算金币和收益
            return resp;
        }

        // 实际产生的原始金币费用等于主播设置的聊天价格
        BigDecimal originCoin = userTo.get().getMessagePrice();
        Asset assetTo = assetRepository.findByUserId(userIdTo);
        Asset assetFrom = assetRepository.findByUserId(userIdFrom);

        // 扣金币
        // TODO: 2020/11/20 判断是否有优惠券，免费券等逻辑

        // 判断金币是否足够
        if (assetFrom.getCoin().compareTo(originCoin) < 0) {
            // TODO: 2020/11/20  金币不足，理论上不应该出现这种情况，记录到日志
            resp.setErrorCode(121002);
            resp.setErrorInfo("用户金币不足");
            return resp;
        }
        // 直接扣金币
        BigDecimal coinFromAfter = assetFrom.getCoin().subtract(originCoin);
        assetFrom.setCoin(coinFromAfter);
        assetRepository.save(assetFrom);

        // 加金币
        // TODO: 2020/11/20 获取抽成 这里先写死
        BigDecimal commissionRatio = BigDecimal.valueOf(0.02);
        BigDecimal commissionCoin = commissionRatio.multiply(originCoin);
        // 直接加金币 去掉抽成价格
        BigDecimal coinToAfter = assetTo.getCoin().add(originCoin.subtract(commissionCoin));
        assetTo.setCoin(coinToAfter);
        assetRepository.save(assetTo);

        // 记录流水
        MessageBill messageBill = new MessageBill();
        messageBill.setUserIdFrom(userIdFrom);
        messageBill.setUserIdTo(userIdTo);
        messageBill.setMsgKey(imAfterSendMsgDto.getMsgKey());
        messageBill.setOriginCoin(originCoin);
        messageBill.setCommissionRatio(commissionRatio);
        messageBill.setCommissionCoin(commissionCoin);
        messageBill.setProfitCoin(coinToAfter);
        messageBillRepository.save(messageBill);

        // TODO: 2020/11/20 记录用户扣金币以及主播加金币的详细日志

        return resp;
    }
}
