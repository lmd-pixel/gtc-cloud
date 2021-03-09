package com.fmisser.gtc.social.repository;

import com.fmisser.gtc.base.dto.social.AnchorGiftBillDto;
import com.fmisser.gtc.base.dto.social.AnchorMessageBillDto;
import com.fmisser.gtc.base.dto.social.CommonBillDto;
import com.fmisser.gtc.base.dto.social.ConsumerMessageBillDto;
import com.fmisser.gtc.base.dto.social.calc.CalcCallProfitDto;
import com.fmisser.gtc.base.dto.social.calc.CalcMessageProfitDto;
import com.fmisser.gtc.social.domain.MessageBill;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface MessageBillRepository extends JpaRepository<MessageBill, Long> {
    // 统计消息收益
    @Query(value = "SELECT " +
            "SUM(commission_coin) AS commission, " +
            "SUM(profit_coin) AS profit, " +
            "SUM(origin_coin) AS origin, " +
            "COUNT(DISTINCT (user_id_from)) AS users " +
            "FROM t_message_bill WHERE " +
            "valid = 1 AND creat_time BETWEEN ?1 AND ?2", nativeQuery = true)
    List<CommonBillDto> calcTotalCoinOnce(Date startTime, Date endTime);

    // 查询主播私信收益列表
    @Query(value = "SELECT SUM(tmb.profit_coin) AS profit, tmb.creat_time AS createTime, " +
            "tu.digit_id AS digitId, tu.nick AS nick, tu.phone AS phone, " +
            "SUM(IF(tmb.source>0,1,0)) AS card " +
            "FROM t_message_bill tmb " +
            "INNER JOIN t_user tu ON tu.id = tmb.user_id_to AND " +
            "(tu.digit_id LIKE CONCAT('%', ?1, '%') OR ?1 IS NULL) AND " +
            "(tu.nick LIKE CONCAT('%', ?2, '%') OR ?2 IS NULL) " +
            "WHERE tmb.valid  = 1 AND " +
            "(tmb.creat_time BETWEEN ?3 AND ?4 OR ?3 IS NULL OR ?4 IS NULL) " +
            "GROUP BY tmb.id ORDER BY tmb.id DESC LIMIT ?5 OFFSET ?6", nativeQuery = true)
//    Page<AnchorMessageBillDto> getAnchorMessageBillList(String digitId, String nick,
//                                                        Date startTime, Date endTime,
//                                                        Pageable pageable);
    List<AnchorMessageBillDto> getAnchorMessageBillList(String digitId, String nick,
                                                        Date startTime, Date endTime,
                                                        int limit, int offset);


    // 查询用户私信以及消费列表
    @Query(value = "SELECT SUM(tmb.origin_coin) AS consume, tmb.creat_time AS createTime, " +
            "tu.digit_id AS consumerDigitId, tu.nick AS consumerNick, tu.phone AS consumerPhone, " +
            "tu2.digit_id AS anchorDigitId, tu2.nick AS anchorNick, tu2.phone AS anchorPhone " +
            "FROM t_message_bill tmb " +
            "INNER JOIN t_user tu ON tu.id = tmb.user_id_from AND " +
            "(tu.digit_id LIKE CONCAT('%', ?1, '%') OR ?1 IS NULL) AND " +
            "(tu.nick LIKE CONCAT('%', ?2, '%') OR ?2 IS NULL) " +
            "INNER JOIN t_user tu2 ON tu2.id = tmb.user_id_to AND " +
            "(tu2.digit_id LIKE CONCAT('%', ?3, '%') OR ?3 IS NULL) AND " +
            "(tu2.nick LIKE CONCAT('%', ?4, '%') OR ?4 IS NULL) " +
            "WHERE tmb.valid  = 1 AND " +
            "(tmb.creat_time BETWEEN ?5 AND ?6 OR ?5 IS NULL OR ?6 IS NULL) " +
            "GROUP BY tmb.id ORDER BY tmb.id DESC LIMIT ?7 OFFSET ?8", nativeQuery = true)
//    Page<ConsumerMessageBillDto> getConsumerMessageBillList(String consumerDigitId, String consumerNick,
//                                                            String anchorDigitId, String anchorNick,
//                                                            Date startTime, Date endTime,
//                                                            Pageable pageable);
    List<ConsumerMessageBillDto> getConsumerMessageBillList(String consumerDigitId, String consumerNick,
                                                            String anchorDigitId, String anchorNick,
                                                            Date startTime, Date endTime,
                                                            int limit, int offset);

    // 统计私信收益相关数据： 数据总条数，收益总额等
    @Query(value = "SELECT " +
            "SUM(tmb.id) AS messageCount, " +
            "COUNT(tmb.id) AS count, " +
            "SUM(tmb.origin_coin) AS consume, " +
            "SUM(tmb.profit_coin) AS profit, " +
            "SUM(tmb.commission_coin) AS commission, " +
            "SUM(DISTINCT tmb.user_id_from) AS users, " +
            "SUM(DISTINCT tmb.user_id_to) AS anchors " +
            "FROM t_message_bill tmb " +
            "INNER JOIN t_user tu ON tu.id = tmb.user_id_from AND " +
            "(tu.digit_id LIKE CONCAT('%', ?1, '%') OR ?1 IS NULL) AND " +
            "(tu.nick LIKE CONCAT('%', ?2, '%') OR ?2 IS NULL) " +
            "INNER JOIN t_user tu2 ON tu2.id = tmb.user_id_to AND " +
            "(tu2.digit_id LIKE CONCAT('%', ?3, '%') OR ?3 IS NULL) AND " +
            "(tu2.nick LIKE CONCAT('%', ?4, '%') OR ?4 IS NULL) " +
            "WHERE tmb.valid = 1 AND " +
            "(tmb.creat_time BETWEEN ?5 AND ?6 OR ?5 IS NULL OR ?6 IS NULL) " +
            "", nativeQuery = true)
    CalcMessageProfitDto calcMessageProfit(String consumerDigitId, String consumerNick,
                                        String anchorDigitId, String anchorNick,
                                        Date startTime, Date endTime);
}

