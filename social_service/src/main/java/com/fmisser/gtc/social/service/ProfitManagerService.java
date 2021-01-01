package com.fmisser.gtc.social.service;

import com.fmisser.gtc.base.dto.social.*;
import com.fmisser.gtc.base.exception.ApiException;
import org.springframework.data.util.Pair;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 收益管理服务
 */

public interface ProfitManagerService {
    // 获取主播通话收益列表
    Pair<List<AnchorCallBillDto>, Map<String, Object>> getAnchorCallProfitList(String digitId, String nick,
                                                                               Date startTime, Date endTime,
                                                                               Integer type,
                                                                               int pageIndex, int pageSize) throws ApiException;

    // 获取主播消息收益列表
    Pair<List<AnchorMessageBillDto>, Map<String, Object>> getAnchorMessageProfitList(String digitId, String nick,
                                                          Date startTime, Date endTime,
                                                          int pageIndex, int pageSize) throws ApiException;

    // 获取主播礼物收益列表
    Pair<List<AnchorGiftBillDto>, Map<String,Object>> getAnchorGiftProfitList(String digitId, String nick,
                                                    Date startTime, Date endTime,
                                                    int pageIndex, int pageSize) throws ApiException;

    // 获取用户通话消费列表
    Pair<List<ConsumerCallBillDto>, Map<String, Object>> getConsumerCallBillList(String consumerDigitId, String consumerNick,
                                                      String anchorDigitId, String anchorNick,
                                                      Date startTime, Date endTime,
                                                      Integer type,
                                                      int pageIndex, int pageSize) throws ApiException;

    // 获取用户私信列表
    Pair<List<ConsumerMessageBillDto>, Map<String, Object>> getConsumerMsgBillList(String consumerDigitId, String consumerNick,
                                                        String anchorDigitId, String anchorNick,
                                                        Date startTime, Date endTime,
                                                        int pageIndex, int pageSize) throws ApiException;

    // 获取用户礼物列表
    Pair<List<ConsumerGiftBillDto>, Map<String, Object>> getConsumerGiftBillList(String consumerDigitId, String consumerNick,
                                                        String anchorDigitId, String anchorNick,
                                                        Date startTime, Date endTime,
                                                        int pageIndex, int pageSize) throws ApiException;
}
