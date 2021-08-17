package com.fmisser.gtc.social.repository;

import com.fmisser.gtc.base.dto.social.RechargeDto;
import com.fmisser.gtc.base.dto.social.StatisticRechargeDto;
import com.fmisser.gtc.base.dto.social.calc.CalcRechargeDto;
import com.fmisser.gtc.social.domain.Recharge;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface RechargeRepository extends JpaRepository<Recharge, Long> {

    Recharge findByOrderNumber(String orderNumber);

    Long countByUserIdAndStatusGreaterThanEqual(Long userId, int status);

    // 获取给定时间的充值人数(去重)，给定时间内充值金额(实际支付金额), 给定时间内的平台收入(实际收到的钱)
    @Query(value = "SELECT " +
            "COUNT(DISTINCT (user_id)) AS users, " +
            "SUM(pay) AS pay, " +
            "SUM(income) AS income " +
            "FROM t_recharge " +
            "WHERE creat_time BETWEEN ?1 AND ?2",
            nativeQuery = true)
    StatisticRechargeDto getRecharge(Date start, Date end);

    // 获取充值列表
    @Query(value = "SELECT tr.order_number AS orderNumber, tr.type AS type, tr.coin AS coin, " +
            "tr.coin_before AS coinBefore, tr.coin_after AS coinAfter, " +
            "tr.price AS price, tr.status AS status, tr.creat_time AS createTime," +
            "tu.digit_id AS digitId, tu.nick AS nick, tu.phone AS phone, tu.channel_id AS channelId " +
            "FROM t_recharge tr " +
            "INNER JOIN t_user tu ON tu.id = tr.user_id AND " +
            "(tu.digit_id LIKE CONCAT('%', ?1, '%') OR ?1 IS NULL) AND " +
            "(tu.nick LIKE CONCAT('%', ?2, '%') OR ?2 IS NULL) AND " +
            "(tu.channel_id LIKE CONCAT('%', ?8, '%') OR ?8 IS NULL) " +
            "WHERE tr.status IN (?5) AND " +
            "(tr.creat_time BETWEEN ?3 AND ?4 OR ?3 IS NULL OR ?4 IS NULL) " +
            "ORDER BY tr.creat_time DESC " +
            "LIMIT ?6 OFFSET ?7",
            nativeQuery = true)
//    Page<RechargeDto> getRechargeList(String digitId, String nick,
//                                      Date startTime, Date endTime,
//                                      List<Integer> status, Pageable pageable);
    List<RechargeDto> getRechargeList(String digitId, String nick,
                                      Date startTime, Date endTime,
                                      List<Integer> status, int limit, int offset,String channelId);

    // 统计总充值
    @Query(value = "SELECT COUNT(tr.id) AS count, SUM(tr.price) AS recharge " +
            "FROM t_recharge tr " +
            "INNER JOIN t_user tu ON tu.id = tr.user_id AND " +
            "(tu.digit_id LIKE CONCAT('%', ?1, '%') OR ?1 IS NULL) AND " +
            "(tu.nick LIKE CONCAT('%', ?2, '%') OR ?2 IS NULL) " +
            "WHERE tr.status IN (?5) AND " +
            "(tr.creat_time BETWEEN ?3 AND ?4 OR ?3 IS NULL OR ?4 IS NULL) ",
            nativeQuery = true)
    CalcRechargeDto calcRecharge(String digitId, String nick,
                                 Date startTime, Date endTime,
                                 List<Integer> status);




}
