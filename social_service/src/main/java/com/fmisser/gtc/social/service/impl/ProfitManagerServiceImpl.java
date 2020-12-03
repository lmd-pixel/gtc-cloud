package com.fmisser.gtc.social.service.impl;

import com.fmisser.gtc.base.dto.social.*;
import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.social.repository.CallBillRepository;
import com.fmisser.gtc.social.repository.GiftBillRepository;
import com.fmisser.gtc.social.repository.MessageBillRepository;
import com.fmisser.gtc.social.service.ProfitManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ProfitManagerServiceImpl implements ProfitManagerService {

    private final CallBillRepository callBillRepository;

    private final MessageBillRepository messageBillRepository;

    private final GiftBillRepository giftBillRepository;

    public ProfitManagerServiceImpl(CallBillRepository callBillRepository,
                                    MessageBillRepository messageBillRepository,
                                    GiftBillRepository giftBillRepository) {
        this.callBillRepository = callBillRepository;
        this.messageBillRepository = messageBillRepository;
        this.giftBillRepository = giftBillRepository;
    }

    @Override
    public List<AnchorCallBillDto> getAnchorCallProfitList(String digitId, String nick,
                                                           Date startTime, Date endTime, Integer type,
                                                           int pageIndex, int pageSize) throws ApiException {
        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        return callBillRepository
                .getAnchorCallBillList(digitId, nick, startTime, endTime, type, pageable)
                .getContent();

    }

    @Override
    public List<AnchorMessageBillDto> getAnchorMessageProfitList(String digitId, String nick,
                                                                 Date startTime, Date endTime,
                                                                 int pageIndex, int pageSize) throws ApiException {
        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        return messageBillRepository
                .getAnchorMessageBillList(digitId, nick, startTime, endTime, pageable)
                .getContent();
    }

    @Override
    public List<AnchorGiftBillDto> getAnchorGiftProfitList(String digitId, String nick,
                                                           Date startTime, Date endTime,
                                                           int pageIndex, int pageSize) throws ApiException {
        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        return giftBillRepository
                .getAnchorGiftBillList(digitId, nick, startTime, endTime, pageable)
                .getContent();
    }

    @Override
    public List<ConsumerCallBillDto> getConsumerCallBillList(String consumerDigitId, String consumerNick,
                                                             String anchorDigitId, String anchorNick,
                                                             Date startTime, Date endTime,
                                                             Integer type,
                                                             int pageIndex, int pageSize) throws ApiException {
        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        Page<ConsumerCallBillDto> consumerCallBillDtoPage =
                callBillRepository.getConsumerCallBillList(consumerDigitId, consumerNick,
                        anchorDigitId, anchorNick, startTime, endTime, type, pageable);
        // TODO: 2020/12/1 统计总的数据
        return consumerCallBillDtoPage.getContent();
    }

    @Override
    public List<ConsumerMessageBillDto> getConsumerMsgBillList(String consumerDigitId, String consumerNick,
                                                               String anchorDigitId, String anchorNick,
                                                               Date startTime, Date endTime,
                                                               int pageIndex, int pageSize) throws ApiException {
        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        Page<ConsumerMessageBillDto> consumerMessageBillDtoPage =
                messageBillRepository.getConsumerMessageBillList(consumerDigitId, consumerNick,
                        anchorDigitId, anchorNick, startTime, endTime, pageable);
        return consumerMessageBillDtoPage.getContent();
    }

    @Override
    public List<ConsumerGiftBillDto> getConsumerGiftBillList(String consumerDigitId, String consumerNick,
                                                                String anchorDigitId, String anchorNick,
                                                                Date startTime, Date endTime,
                                                                int pageIndex, int pageSize) throws ApiException {
        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        Page<ConsumerGiftBillDto> consumerGiftBillDtoPage =
                giftBillRepository.getConsumerGiftBillList(consumerDigitId, consumerNick,
                        anchorDigitId, anchorNick, startTime, endTime, pageable);
        return consumerGiftBillDtoPage.getContent();
    }
}
