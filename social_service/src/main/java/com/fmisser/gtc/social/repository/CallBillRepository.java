package com.fmisser.gtc.social.repository;

import com.fmisser.gtc.base.dto.social.AnchorCallBillDto;
import com.fmisser.gtc.base.dto.social.CommonBillDto;
import com.fmisser.gtc.base.dto.social.ConsumerCallBillDto;
import com.fmisser.gtc.base.dto.social.calc.CalcAnchorProfitDto;
import com.fmisser.gtc.base.dto.social.calc.CalcCallProfitDto;
import com.fmisser.gtc.base.dto.social.calc.CalcConsumerProfitDto;
import com.fmisser.gtc.social.domain.CallBill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface CallBillRepository extends JpaRepository<CallBill, Long> {

    List<CallBill> findByCallId(Long callId);

    List<CallBill> findByCallIdAndSourceNot(Long callId, int source);

    // 查询音视频通话的统计数据, 平台收入，主播收益，用户消耗，用户人数
    // row1是语音 row2是视频
    @Query(value = "SELECT " +
            "SUM(commission_coin) AS commission, " +
            "SUM(profit_coin) AS profit, " +
            "SUM(origin_coin) AS origin, " +
            "COUNT(DISTINCT (user_id_from)) AS users " +
            "FROM t_call_bill tcb WHERE " +
            "valid = 1 AND (creat_time BETWEEN ?1 AND ?2 OR ?1 IS NULL OR ?2 IS NULL) " +
            "GROUP BY type", nativeQuery = true)
    List<CommonBillDto> calcTotalCoinOnce(Date startTime, Date endTime);

    // 查询主播通话以及收益列表
    @Query(value = "SELECT tc.duration AS duration, tc.created_time AS createTime, " +
            "SUM(tcb.profit_coin) AS profit, " +
            "SUM(IF(tcb.source>0,1,0)) AS card, " +
            "MAX(tu.digit_id) AS digitId, MAX(tu.nick) AS nick " +
            "FROM t_call tc " +
            "INNER JOIN t_user tu ON " +
            "((tc.call_mode=0 AND tu.id = tc.user_id_to) OR (tc.call_mode=1 AND tu.id = tc.user_id_from)) AND " +
            "(tu.digit_id LIKE CONCAT('%', ?1, '%') OR ?1 IS NULL) AND " +
            "(tu.nick LIKE CONCAT('%', ?2, '%') OR ?2 IS NULL) " +
            "LEFT JOIN t_call_bill tcb ON tcb.call_id = tc.id AND valid = 1 " +
            "WHERE tc.type = ?5 AND " +
            "(tc.created_time BETWEEN ?3 AND ?4 OR ?3 IS NULL OR ?4 IS NULL) " +
            "GROUP BY tc.id ORDER BY tc.id DESC LIMIT ?6 OFFSET ?7", nativeQuery = true)
//    Page<AnchorCallBillDto> getAnchorCallBillList(String digitId, String nick,
//                                                  Date startTime, Date endTime,
//                                                  Integer type, Pageable pageable);
    List<AnchorCallBillDto> getAnchorCallBillList(String digitId, String nick,
                                                  Date startTime, Date endTime,
                                                  Integer type, int limit, int offset);

    // 查询用户通话以及消费列表
    @Query(value = "SELECT tc.duration AS duration, tc.created_time AS createTime, " +
            "SUM(tcb.origin_coin) AS consume, " +
            "tu.digit_id AS consumerDigitId, tu.nick AS consumerNick, tu.phone AS consumerPhone, " +
            "tu2.digit_id AS anchorDigitId, tu2.nick AS anchorNick, tu2.phone AS anchorPhone " +
            "FROM t_call tc " +
            "INNER JOIN t_user tu ON tu.id = tc.user_id_from AND " +
            "(tu.digit_id LIKE CONCAT('%', ?1, '%') OR ?1 IS NULL) AND " +
            "(tu.nick LIKE CONCAT('%', ?2, '%') OR ?2 IS NULL) " +
            "INNER JOIN t_user tu2 ON tu2.id = tc.user_id_to AND " +
            "(tu2.digit_id LIKE CONCAT('%', ?3, '%') OR ?3 IS NULL) AND " +
            "(tu2.nick LIKE CONCAT('%', ?4, '%') OR ?4 IS NULL) " +
            "LEFT JOIN t_call_bill tcb ON tcb.call_id = tc.id AND valid = 1 " +
            "WHERE tc.is_finished = 1 AND tc.type = ?7 AND " +
            "(tc.created_time BETWEEN ?5 AND ?6 OR ?5 IS NULL OR ?6 IS NULL) " +
            "GROUP BY tc.id ORDER BY tc.id DESC LIMIT ?8 OFFSET ?9", nativeQuery = true)
//    Page<ConsumerCallBillDto> getConsumerCallBillList(String consumerDigitId, String consumerNick,
//                                                      String anchorDigitId, String anchorNick,
//                                                      Date startTime, Date endTime,
//                                                      Integer type, Pageable pageable);
    List<ConsumerCallBillDto> getConsumerCallBillList(String consumerDigitId, String consumerNick,
                                                      String anchorDigitId, String anchorNick,
                                                      Date startTime, Date endTime,
                                                      Integer type, int limit, int offset);


    // 统计通话收益相关数据： 数据总条数，收益总额等
    @Query(value = "SELECT " +
            "SUM(tc.duration) AS duration, " +
            "COUNT(DISTINCT tc.id) AS count, " +
            "SUM(tcb.origin_coin) AS consume, " +
            "SUM(tcb.profit_coin) AS profit, " +
            "SUM(tcb.commission_coin) AS commission, " +
            "COUNT(DISTINCT tc.user_id_from) AS users, " +
            "COUNT(DISTINCT tc.user_id_to) AS anchors " +
            "FROM t_call tc " +
            "INNER JOIN t_user tu ON tu.id = tc.user_id_from AND " +
            "(tu.digit_id LIKE CONCAT('%', ?1, '%') OR ?1 IS NULL) AND " +
            "(tu.nick LIKE CONCAT('%', ?2, '%') OR ?2 IS NULL) " +
            "INNER JOIN t_user tu2 ON tu2.id = tc.user_id_to AND " +
            "(tu2.digit_id LIKE CONCAT('%', ?3, '%') OR ?3 IS NULL) AND " +
            "(tu2.nick LIKE CONCAT('%', ?4, '%') OR ?4 IS NULL) " +
            "LEFT JOIN t_call_bill tcb ON tcb.call_id = tc.id AND valid = 1 " +
            "WHERE tc.type = ?7 AND " +
            "(tc.created_time BETWEEN ?5 AND ?6 OR ?5 IS NULL OR ?6 IS NULL) " +
            "", nativeQuery = true)
    CalcCallProfitDto calcCallProfit(String consumerDigitId, String consumerNick,
                                     String anchorDigitId, String anchorNick,
                                     Date startTime, Date endTime,
                                     Integer type);



    // 获取主播总收益，视频收益，语音收益，私信收益，礼物收益
    @Query(value = "SELECT\n" +
            "       SUM(t_profit.videoProfit+t_profit.voiceProfit+t_profit.msgProfit+t_profit.giftProfit) AS totalProfit,\n" +
            "       SUM(t_profit.voiceProfit) AS voiceProfit,\n" +
            "       SUM(t_profit.videoProfit) AS videoProfit,\n" +
            "       SUM(t_profit.msgProfit) AS msgProfit,\n" +
            "       SUM(t_profit.giftProfit) AS giftProfit\n" +
            "FROM\n" +
            "(SELECT\n" +
            "       SUM(IF(tcb.type=0,tcb.profit_coin,0)) AS voiceProfit,\n" +
            "       SUM(IF(tcb.type=1,tcb.profit_coin,0)) AS videoProfit,\n" +
            "       0 AS msgProfit, 0 AS giftProfit\n" +
            "FROM t_call_bill tcb\n" +
            "    INNER JOIN t_user tu\n" +
            "        ON tu.id = tcb.user_id_to AND\n" +
            "           (tu.digit_id LIKE CONCAT('%', ?1, '%') OR ?1 IS NULL) AND\n" +
            "           (tu.nick LIKE CONCAT('%', ?2, '%') OR ?2 IS NULL)\n" +
            "WHERE tcb.creat_time BETWEEN ?3 AND ?4 OR ?3 IS NULL OR ?4 IS NULL\n" +
            "UNION ALL\n" +
            "SELECT\n" +
            "       0 AS voiceProfit, 0 AS videoProfit,\n" +
            "       SUM(tmb.profit_coin) AS msgProfit,\n" +
            "       0 AS giftProfit\n" +
            "FROM t_message_bill tmb\n" +
            "    INNER JOIN t_user tu\n" +
            "        ON tu.id = tmb.user_id_to AND\n" +
            "            (tu.digit_id LIKE CONCAT('%', ?1, '%') OR ?1 IS NULL) AND\n" +
            "            (tu.nick LIKE CONCAT('%', ?2, '%') OR ?2 IS NULL)\n" +
            "WHERE tmb.creat_time BETWEEN ?3 AND ?4 OR ?3 IS NULL OR ?4 IS NULL\n" +
            "UNION ALL\n" +
            "SELECT 0 AS voiceProfit, 0 AS videoProfit, 0 AS msgProfit,\n" +
            "       SUM(tgb.profit_coin) AS giftProfit\n" +
            "FROM t_gift_bill tgb\n" +
            "    INNER JOIN t_user tu\n" +
            "        ON tu.id = tgb.user_id_to AND\n" +
            "            (tu.digit_id LIKE CONCAT('%', ?1, '%') OR ?1 IS NULL) AND\n" +
            "            (tu.nick LIKE CONCAT('%', ?2, '%') OR ?2 IS NULL)\n" +
            "WHERE tgb.creat_time BETWEEN ?3 AND ?4 OR ?3 IS NULL OR ?4 IS NULL)\n" +
            "    t_profit", nativeQuery = true)
    CalcAnchorProfitDto getAnchorProfit(String digitId, String nick, Date startTime, Date endTime);


    @Query(value = "SELECT\n" +
            "SUM(t_profit.videoProfit+t_profit.voiceProfit+t_profit.msgProfit+t_profit.giftProfit) AS totalProfit,\n" +
            "SUM(t_profit.voiceProfit) AS voiceProfit,\n" +
            "SUM(t_profit.videoProfit) AS videoProfit,\n" +
            "SUM(t_profit.msgProfit) AS msgProfit,\n" +
            " SUM(t_profit.giftProfit) AS giftProfit\n" +
            "FROM\n" +
            "(SELECT\n " +
            "0 AS voiceProfit,\n" +
            "SUM(IF(tcb.type=1,tcb.origin_coin,0)) AS videoProfit,\n" +
            "0 AS msgProfit, 0 AS giftProfit\n" +
            "FROM t_call tc\n " +
            "INNER JOIN t_user tu ON tu.id = tc.user_id_from AND\n " +
            "(tu.digit_id LIKE CONCAT('%', ?1, '%') OR ?1 IS NULL) AND \n" +
            "(tu.nick LIKE CONCAT('%', ?2, '%') OR ?2 IS NULL)\n " +
            "INNER JOIN t_user tu2 ON tu2.id = tc.user_id_to AND\n " +
            "(tu2.digit_id LIKE CONCAT('%', ?3, '%') OR ?3 IS NULL) AND\n " +
            "(tu2.nick LIKE CONCAT('%', ?4, '%') OR ?4 IS NULL) \n" +
            "LEFT JOIN t_call_bill tcb ON tcb.call_id = tc.id AND valid = 1 " +
            "WHERE tc.type = 1 AND " +
            "(tc.created_time BETWEEN ?5 AND ?6 OR ?5 IS NULL OR ?6 IS NULL)\n " +
            "UNION ALL\n" +
            "SELECT\n " +
            "SUM(IF(tcb.type=0,tcb.origin_coin,0)) AS voiceProfit,\n" +
            "0 AS videoProfit,\n" +
            "0 AS msgProfit, 0 AS giftProfit\n" +
            "FROM t_call tc\n " +
            "INNER JOIN t_user tu ON tu.id = tc.user_id_from AND\n " +
            "(tu.digit_id LIKE CONCAT('%', ?1, '%') OR ?1 IS NULL) AND \n" +
            "(tu.nick LIKE CONCAT('%', ?2, '%') OR ?2 IS NULL)\n " +
            "INNER JOIN t_user tu2 ON tu2.id = tc.user_id_to AND\n " +
            "(tu2.digit_id LIKE CONCAT('%', ?3, '%') OR ?3 IS NULL) AND\n " +
            "(tu2.nick LIKE CONCAT('%', ?4, '%') OR ?4 IS NULL) \n" +
            "LEFT JOIN t_call_bill tcb ON tcb.call_id = tc.id AND valid = 1 " +
            "WHERE tc.type = 0 AND " +
            "(tc.created_time BETWEEN ?5 AND ?6 OR ?5 IS NULL OR ?6 IS NULL)\n " +
            "UNION ALL\n" +
            "SELECT\n " +
                "0 AS voiceProfit, 0 AS videoProfit,\n" +
                "SUM(tmb.origin_coin) AS msgProfit,0 AS giftProfit\n " +
                "FROM t_message_bill tmb\n " +
                "INNER JOIN t_user tu ON tu.id = tmb.user_id_from AND \n" +
                "(tu.digit_id LIKE CONCAT('%', ?1, '%') OR ?1 IS NULL) AND\n " +
                "(tu.nick LIKE CONCAT('%', ?2, '%') OR ?2 IS NULL) \n" +
                "INNER JOIN t_user tu2 ON tu2.id = tmb.user_id_to AND \n" +
                "(tu2.digit_id LIKE CONCAT('%', ?3, '%') OR ?3 IS NULL) AND\n " +
                "(tu2.nick LIKE CONCAT('%', ?4, '%') OR ?4 IS NULL) " +
                "WHERE tmb.valid = 1 AND " +
                "(tmb.creat_time BETWEEN ?5 AND ?6 OR ?5 IS NULL OR ?6 IS NULL) \n"+
            "UNION ALL\n" +
            "SELECT  0 AS voiceProfit, 0 AS videoProfit, 0 AS msgProfit,\n " +
            "SUM(tgb.origin_coin) AS giftProfit " +
            "FROM t_gift_bill tgb " +
            "INNER JOIN t_user tu ON tu.id = tgb.user_id_from AND " +
            "(tu.digit_id LIKE CONCAT('%', ?1, '%') OR ?1 IS NULL) AND " +
            "(tu.nick LIKE CONCAT('%', ?2, '%') OR ?2 IS NULL) " +
            "INNER JOIN t_user tu2 ON tu2.id = tgb.user_id_to AND " +
            "(tu2.digit_id LIKE CONCAT('%', ?3, '%') OR ?3 IS NULL) AND " +
            "(tu2.nick LIKE CONCAT('%', ?4, '%') OR ?4 IS NULL) " +
            "WHERE tgb.valid = 1 AND " +
            "(tgb.creat_time BETWEEN ?5 AND ?6 OR ?5 IS NULL OR ?6 IS NULL)) " +
            "    t_profit", nativeQuery = true)
    CalcConsumerProfitDto getConsumerProfit(String consumerDigitId, String consumerNick,
                                            String anchorDigitId, String anchorNick,
                                            Date startTime, Date endTime);



}
