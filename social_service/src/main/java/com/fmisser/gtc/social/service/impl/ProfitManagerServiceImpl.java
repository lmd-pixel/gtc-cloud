package com.fmisser.gtc.social.service.impl;

import com.fmisser.gtc.base.dto.social.*;
import com.fmisser.gtc.base.dto.social.calc.CalcAnchorProfitDto;
import com.fmisser.gtc.base.dto.social.calc.CalcCallProfitDto;
import com.fmisser.gtc.base.dto.social.calc.CalcGiftProfitDto;
import com.fmisser.gtc.base.dto.social.calc.CalcMessageProfitDto;
import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.social.repository.CallBillRepository;
import com.fmisser.gtc.social.repository.GiftBillRepository;
import com.fmisser.gtc.social.repository.MessageBillRepository;
import com.fmisser.gtc.social.service.ProfitManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public Pair<List<AnchorCallBillDto>, Map<String, Object>> getAnchorCallProfitList(String digitId, String nick,
                                                                                      Date startTime, Date endTime, Integer type,
                                                                                      int pageIndex, int pageSize) throws ApiException {
        // 如果要使用Page，
        // 要提供countQuery，如果不提供countQuery，jpa会根据query的语句尝试去改造成countQuery，
        // 然后去掉 group by 、order by 等，但不会改变你的查询条件
        // 因为我们这里还要统计其他数据，所以直接重新调用一个新的sql，
        // 注意：countQuery 也会执行一次，除非query查询的行数还不够pageSize, 那不会执行
//        Pageable pageable = PageRequest.of(pageIndex - 1, pageSize);
//        Page<AnchorCallBillDto> anchorCallBillDtoPage = callBillRepository
//                .getAnchorCallBillList(digitId, nick, startTime, endTime, type, pageable);

//        anchorCallBillDtoPage.getTotalElements();
//        anchorCallBillDtoPage.getTotalPages();

        List<AnchorCallBillDto> anchorCallBillDtoPage = callBillRepository
                .getAnchorCallBillList(digitId, nick, startTime, endTime, type, pageSize, (pageIndex - 1) * pageSize);

        CalcCallProfitDto calcCallProfitDto =
                callBillRepository.calcCallProfit(null, null, digitId, nick, startTime, endTime, type);

        Map<String, Object> extra = new HashMap<>();
        extra.put("totalPage", (calcCallProfitDto.getCount() / pageSize) + 1 );
        extra.put("totalEle", calcCallProfitDto.getCount());
        extra.put("currPage", pageIndex);
        extra.put("duration", calcCallProfitDto.getDuration());
        extra.put("profit", calcCallProfitDto.getProfit());
        extra.put("commission", calcCallProfitDto.getCommission());
        extra.put("consume", calcCallProfitDto.getConsume());

        return Pair.of(anchorCallBillDtoPage, extra);
    }

    @Override
    public Pair<List<AnchorMessageBillDto>,Map<String, Object>> getAnchorMessageProfitList(String digitId, String nick,
                                                                 Date startTime, Date endTime,
                                                                 int pageIndex, int pageSize) throws ApiException {
//        Pageable pageable = PageRequest.of(pageIndex, pageSize);
//        return messageBillRepository
//                .getAnchorMessageBillList(digitId, nick, startTime, endTime, pageable)
//                .getContent();

        List<AnchorMessageBillDto> messageBillDtoList = messageBillRepository
                .getAnchorMessageBillList(digitId, nick, startTime, endTime, pageSize, (pageIndex - 1) * pageSize);

        CalcMessageProfitDto calcMessageProfitDto = messageBillRepository
                .calcMessageProfit(null, null, digitId, nick, startTime, endTime);

        Map<String, Object> extra = new HashMap<>();
        extra.put("totalPage", (calcMessageProfitDto.getCount() / pageSize) + 1 );
        extra.put("totalEle", calcMessageProfitDto.getCount());
        extra.put("currPage", pageIndex);
        extra.put("messageCount", calcMessageProfitDto.getMessageCount());
        extra.put("profit", calcMessageProfitDto.getProfit());
        extra.put("commission", calcMessageProfitDto.getCommission());
        extra.put("consume", calcMessageProfitDto.getConsume());

        return Pair.of(messageBillDtoList, extra);
    }

    @Override
    public Pair<List<AnchorGiftBillDto>, Map<String,Object>> getAnchorGiftProfitList(String digitId, String nick,
                                                           Date startTime, Date endTime,
                                                           int pageIndex, int pageSize) throws ApiException {
//        Pageable pageable = PageRequest.of(pageIndex, pageSize);
//        return giftBillRepository
//                .getAnchorGiftBillList(digitId, nick, startTime, endTime, pageable)
//                .getContent();

        List<AnchorGiftBillDto> anchorGiftBillDtoList = giftBillRepository
                .getAnchorGiftBillList(digitId, nick, startTime, endTime, pageSize, (pageIndex - 1) * pageSize);

        CalcGiftProfitDto calcGiftProfitDto = giftBillRepository
                .calcGiftProfit(null, null, digitId, nick, startTime, endTime);

        Map<String, Object> extra = new HashMap<>();
        extra.put("totalPage", (calcGiftProfitDto.getCount() / pageSize) + 1 );
        extra.put("totalEle", calcGiftProfitDto.getCount());
        extra.put("currPage", pageIndex);
        extra.put("giftCount", calcGiftProfitDto.getGiftCount());
        extra.put("profit", calcGiftProfitDto.getProfit());
        extra.put("commission", calcGiftProfitDto.getCommission());
        extra.put("consume", calcGiftProfitDto.getConsume());

        return Pair.of(anchorGiftBillDtoList, extra);
    }

    @Override
    public Pair<List<ConsumerCallBillDto>, Map<String, Object>> getConsumerCallBillList(String consumerDigitId, String consumerNick,
                                                             String anchorDigitId, String anchorNick,
                                                             Date startTime, Date endTime,
                                                             Integer type,
                                                             int pageIndex, int pageSize) throws ApiException {
//        Pageable pageable = PageRequest.of(pageIndex, pageSize);
//        Page<ConsumerCallBillDto> consumerCallBillDtoPage =
//                callBillRepository.getConsumerCallBillList(consumerDigitId, consumerNick,
//                        anchorDigitId, anchorNick, startTime, endTime, type, pageable);
//        // TODO: 2020/12/1 统计总的数据
//        return consumerCallBillDtoPage.getContent();

        List<ConsumerCallBillDto> consumerCallBillDtoList =
                callBillRepository.getConsumerCallBillList(consumerDigitId, consumerNick,
                        anchorDigitId, anchorNick, startTime, endTime, type, pageSize, (pageIndex - 1) * pageSize);

        CalcCallProfitDto calcCallProfitDto =
                callBillRepository.calcCallProfit(consumerDigitId, consumerNick, anchorDigitId, anchorNick
                        , startTime, endTime, type);

        Map<String, Object> extra = new HashMap<>();
        extra.put("totalPage", (calcCallProfitDto.getCount() / pageSize) + 1 );
        extra.put("totalEle", calcCallProfitDto.getCount());
        extra.put("currPage", pageIndex);
        extra.put("duration", calcCallProfitDto.getDuration());
        extra.put("profit", calcCallProfitDto.getProfit());
        extra.put("commission", calcCallProfitDto.getCommission());
        extra.put("consume", calcCallProfitDto.getConsume());

        return Pair.of(consumerCallBillDtoList, extra);

    }

    @Override
    public Pair<List<ConsumerMessageBillDto>,Map<String, Object>> getConsumerMsgBillList(String consumerDigitId, String consumerNick,
                                                               String anchorDigitId, String anchorNick,
                                                               Date startTime, Date endTime,
                                                               int pageIndex, int pageSize) throws ApiException {
//        Pageable pageable = PageRequest.of(pageIndex, pageSize);
//        Page<ConsumerMessageBillDto> consumerMessageBillDtoPage =
//                messageBillRepository.getConsumerMessageBillList(consumerDigitId, consumerNick,
//                        anchorDigitId, anchorNick, startTime, endTime, pageable);
//        return consumerMessageBillDtoPage.getContent();

        List<ConsumerMessageBillDto> consumerMessageBillDtoList =
                messageBillRepository.getConsumerMessageBillList(consumerDigitId, consumerNick,
                        anchorDigitId, anchorNick, startTime, endTime, pageSize, (pageIndex - 1) * pageSize);

        CalcMessageProfitDto calcMessageProfitDto = messageBillRepository
                .calcMessageProfit(consumerDigitId, consumerNick, anchorDigitId, anchorNick, startTime, endTime);

        Map<String, Object> extra = new HashMap<>();
        extra.put("totalPage", (calcMessageProfitDto.getCount() / pageSize) + 1 );
        extra.put("totalEle", calcMessageProfitDto.getCount());
        extra.put("currPage", pageIndex);
        extra.put("messageCount", calcMessageProfitDto.getMessageCount());
        extra.put("profit", calcMessageProfitDto.getProfit());
        extra.put("commission", calcMessageProfitDto.getCommission());
        extra.put("consume", calcMessageProfitDto.getConsume());

        return Pair.of(consumerMessageBillDtoList, extra);
    }

    @Override
    public Pair<List<ConsumerGiftBillDto>, Map<String, Object>> getConsumerGiftBillList(String consumerDigitId, String consumerNick,
                                                                String anchorDigitId, String anchorNick,
                                                                Date startTime, Date endTime,
                                                                int pageIndex, int pageSize) throws ApiException {
//        Pageable pageable = PageRequest.of(pageIndex, pageSize);
//        Page<ConsumerGiftBillDto> consumerGiftBillDtoPage =
//                giftBillRepository.getConsumerGiftBillList(consumerDigitId, consumerNick,
//                        anchorDigitId, anchorNick, startTime, endTime, pageable);
//        return consumerGiftBillDtoPage.getContent();

        List<ConsumerGiftBillDto> consumerGiftBillDtoList = giftBillRepository
                .getConsumerGiftBillList(consumerDigitId, consumerNick, anchorDigitId, anchorNick, startTime, endTime, pageSize, (pageIndex - 1) * pageSize);

        CalcGiftProfitDto calcGiftProfitDto = giftBillRepository
                .calcGiftProfit(consumerDigitId, consumerNick, anchorDigitId, anchorNick, startTime, endTime);

        Map<String, Object> extra = new HashMap<>();
        extra.put("totalPage", (calcGiftProfitDto.getCount() / pageSize) + 1 );
        extra.put("totalEle", calcGiftProfitDto.getCount());
        extra.put("currPage", pageIndex);
        extra.put("giftCount", calcGiftProfitDto.getGiftCount());
        extra.put("profit", calcGiftProfitDto.getProfit());
        extra.put("commission", calcGiftProfitDto.getCommission());
        extra.put("consume", calcGiftProfitDto.getConsume());

        return Pair.of(consumerGiftBillDtoList, extra);
    }

    @Override
    public CalcAnchorProfitDto getAnchorProfit(String digitId, String nick, Date startTime, Date endTime) throws ApiException {
        return callBillRepository.getAnchorProfit(digitId, nick, startTime, endTime);
    }
}
