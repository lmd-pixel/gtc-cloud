package com.fmisser.gtc.social.repository;

import com.fmisser.gtc.base.dto.social.AnchorCallBillDto;
import com.fmisser.gtc.base.dto.social.AnchorGiftBillDto;
import com.fmisser.gtc.base.dto.social.CommonBillDto;
import com.fmisser.gtc.base.dto.social.ConsumerGiftBillDto;
import com.fmisser.gtc.base.dto.social.calc.CalcGiftProfitDto;
import com.fmisser.gtc.base.dto.social.calc.CalcMessageProfitDto;
import com.fmisser.gtc.social.domain.GiftBill;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface GiftBillRepository extends JpaRepository<GiftBill, Long> {
    // 统计礼物的收益
    @Query(value = "SELECT " +
            "SUM(commission_coin) AS commission, " +
            "SUM(profit_coin) AS profit, " +
            "SUM(origin_coin) AS origin, " +
            "COUNT(DISTINCT (user_id_from)) AS users " +
            "FROM t_gift_bill WHERE " +
            "valid = 1 AND " +
            "(creat_time BETWEEN ?1 AND ?2 OR ?1 IS NULL OR ?2 IS NULL)", nativeQuery = true)
    List<CommonBillDto> calcTotalCoinOnce(Date startTime, Date endTime);

    // 查询主播礼物收益列表
    @Query(value = "SELECT SUM(tgb.profit_coin) AS profit, tgb.creat_time AS createTime, " +
            "tg.name AS giftName, " +
            "tu.digit_id AS digitId, tu.nick AS nick, tu.phone AS phone " +
            "FROM t_gift_bill tgb " +
            "INNER JOIN t_user tu ON tu.id = tgb.user_id_to AND " +
            "(tu.digit_id LIKE CONCAT('%', ?1, '%') OR ?1 IS NULL) AND " +
            "(tu.nick LIKE CONCAT('%', ?2, '%') OR ?2 IS NULL) " +
            "LEFT JOIN t_gift tg ON tg.id = tgb.gift_id " +
            "WHERE tgb.valid  = 1 AND " +
            "(tgb.creat_time BETWEEN ?3 AND ?4 OR ?3 IS NULL OR ?4 IS NULL) " +
            "GROUP BY tgb.id ORDER BY tgb.id DESC LIMIT ?5 OFFSET ?6", nativeQuery = true)
//    Page<AnchorGiftBillDto> getAnchorGiftBillList(String digitId, String nick,
//                                                  Date startTime, Date endTime,
//                                                  Pageable pageable);
    List<AnchorGiftBillDto> getAnchorGiftBillList(String digitId, String nick,
                                                  Date startTime, Date endTime,
                                                  int limit, int offset);


    // 查询用户礼物消费列表
    @Query(value = "SELECT SUM(tgb.origin_coin) AS consume, tgb.creat_time AS createTime, " +
            "tg.name AS giftName, " +
            "tu.digit_id AS consumerDigitId, tu.nick AS consumerNick, tu.phone AS consumerPhone, " +
            "tu2.digit_id AS anchorDigitId, tu2.nick AS anchorNick, tu2.phone AS anchorPhone " +
            "FROM t_gift_bill tgb " +
            "INNER JOIN t_user tu ON tu.id = tgb.user_id_from AND " +
            "(tu.digit_id LIKE CONCAT('%', ?1, '%') OR ?1 IS NULL) AND " +
            "(tu.nick LIKE CONCAT('%', ?2, '%') OR ?2 IS NULL) " +
            "INNER JOIN t_user tu2 ON tu2.id = tgb.user_id_to AND " +
            "(tu2.digit_id LIKE CONCAT('%', ?3, '%') OR ?3 IS NULL) AND " +
            "(tu2.nick LIKE CONCAT('%', ?4, '%') OR ?4 IS NULL) " +
            "LEFT JOIN t_gift tg ON tg.id = tgb.gift_id " +
            "WHERE tgb.valid  = 1 AND " +
            "(tgb.creat_time BETWEEN ?5 AND ?6 OR ?5 IS NULL OR ?6 IS NULL) " +
            "GROUP BY tgb.id ORDER BY tgb.id DESC LIMIT ?7 OFFSET ?8", nativeQuery = true)
//    Page<ConsumerGiftBillDto> getConsumerGiftBillList(String consumerDigitId, String consumerNick,
//                                                      String anchorDigitId, String anchorNick,
//                                                      Date startTime, Date endTime,
//                                                      Pageable pageable);
    List<ConsumerGiftBillDto> getConsumerGiftBillList(String consumerDigitId, String consumerNick,
                                                      String anchorDigitId, String anchorNick,
                                                      Date startTime, Date endTime,
                                                      int limit, int offset);

    // 统计礼物收益相关数据： 数据总条数，收益总额等
    @Query(value = "SELECT " +
            "SUM(tgb.id) AS giftCount, " +
            "COUNT(tgb.id) AS count, " +
            "SUM(tgb.origin_coin) AS consume, " +
            "SUM(tgb.profit_coin) AS profit, " +
            "SUM(tgb.commission_coin) AS commission, " +
            "SUM(DISTINCT tgb.user_id_from) AS users, " +
            "SUM(DISTINCT tgb.user_id_to) AS anchors " +
            "FROM t_gift_bill tgb " +
            "INNER JOIN t_user tu ON tu.id = tgb.user_id_from AND " +
            "(tu.digit_id LIKE CONCAT('%', ?1, '%') OR ?1 IS NULL) AND " +
            "(tu.nick LIKE CONCAT('%', ?2, '%') OR ?2 IS NULL) " +
            "INNER JOIN t_user tu2 ON tu2.id = tgb.user_id_to AND " +
            "(tu2.digit_id LIKE CONCAT('%', ?3, '%') OR ?3 IS NULL) AND " +
            "(tu2.nick LIKE CONCAT('%', ?4, '%') OR ?4 IS NULL) " +
            "WHERE tgb.valid = 1 AND " +
            "(tgb.creat_time BETWEEN ?5 AND ?6 OR ?5 IS NULL OR ?6 IS NULL) " +
            "", nativeQuery = true)
    CalcGiftProfitDto calcGiftProfit(String consumerDigitId, String consumerNick,
                                        String anchorDigitId, String anchorNick,
                                        Date startTime, Date endTime);
}
