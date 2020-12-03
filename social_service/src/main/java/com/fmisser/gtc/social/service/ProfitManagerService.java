package com.fmisser.gtc.social.service;

import com.fmisser.gtc.base.dto.social.*;
import com.fmisser.gtc.base.exception.ApiException;

import java.util.Date;
import java.util.List;

/**
 * 收益管理服务
 */

public interface ProfitManagerService {
    // 获取主播通话收益列表
    List<AnchorCallBillDto> getAnchorCallProfitList(String digitId, String nick,
                                                    Date startTime, Date endTime,
                                                    Integer type,
                                                    int pageIndex, int pageSize) throws ApiException;

    // 获取主播消息收益列表
    List<AnchorMessageBillDto> getAnchorMessageProfitList(String digitId, String nick,
                                                          Date startTime, Date endTime,
                                                          int pageIndex, int pageSize) throws ApiException;

    // 获取主播礼物收益列表
    List<AnchorGiftBillDto> getAnchorGiftProfitList(String digitId, String nick,
                                                    Date startTime, Date endTime,
                                                    int pageIndex, int pageSize) throws ApiException;

    // 获取用户通话消费列表
    List<ConsumerCallBillDto> getConsumerCallBillList(String consumerDigitId, String consumerNick,
                                                      String anchorDigitId, String anchorNick,
                                                      Date startTime, Date endTime,
                                                      Integer type,
                                                      int pageIndex, int pageSize) throws ApiException;

    // 获取用户私信列表
    List<ConsumerMessageBillDto> getConsumerMsgBillList(String consumerDigitId, String consumerNick,
                                                        String anchorDigitId, String anchorNick,
                                                        Date startTime, Date endTime,
                                                        int pageIndex, int pageSize) throws ApiException;

    // 获取用户礼物列表
    List<ConsumerGiftBillDto> getConsumerGiftBillList(String consumerDigitId, String consumerNick,
                                                        String anchorDigitId, String anchorNick,
                                                        Date startTime, Date endTime,
                                                        int pageIndex, int pageSize) throws ApiException;
}
