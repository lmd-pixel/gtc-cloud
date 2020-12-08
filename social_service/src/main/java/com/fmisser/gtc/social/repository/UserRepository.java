package com.fmisser.gtc.social.repository;

import com.fmisser.gtc.base.dto.social.AnchorDto;
import com.fmisser.gtc.base.dto.social.ConsumerDto;
import com.fmisser.gtc.social.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUsername(String username);
    long countByCreateTimeBetween(Date start, Date end);
    Optional<User> findByUsername(String username);
    Optional<User> findByDigitId(String digitId);
    Page<User> findByIdentityOrderByCreateTimeDesc(int identity, Pageable pageable);

    // 获取主播列表
    @Query(value = "SELECT * FROM t_user WHERE identity = 1 ORDER BY create_time DESC ",
            nativeQuery = true)
    Page<Long> getAnchorList(Pageable pageable);

    // 获取总注册人数，给定时间内的注册人数，认证用户总人数，给定时间内认证用户人数
    @Query(value = "SELECT COUNT(*) FROM t_user UNION ALL " +
            "SELECT COUNT(*) FROM t_user WHERE create_time BETWEEN ?1 AND ?2 UNION ALL " +
            "SELECT COUNT(*) FROM t_user WHERE identity = 1 UNION ALL " +
            "SELECT COUNT(*) FROM t_user WHERE identity = 1 AND create_time BETWEEN ?1 AND ?2",
            nativeQuery = true)
    List<Long> userStatistics(Date start, Date end);


    // 主播数据模糊查询
    @Query(value = "SELECT tu.digit_id AS digitId, tu.nick AS nick, tu.phone AS phone, tu.gender AS gender, " +
            "tu.follows AS follows, tu.create_time AS createTime, " +
            "SUM(tc.duration) AS audioDuration, " +
            "SUM(tc2.duration) AS videoDuration, " +
            "SUM(tcb.profit_coin) AS audioProfit, " +
            "SUM(tcb2.profit_coin) AS videoProfit, " +
            "SUM(tmb.profit_coin) AS messageProfit, " +
            "SUM(tgb.profit_coin) AS giftProfit, " +
            "tas.coin As coin, " +
            "MAX(ta.active_time) AS activeTime " +
            "FROM t_user tu " +
            "LEFT JOIN t_call tc ON tc.user_id_to = tu.id AND tc.type = 0 " +
            "LEFT JOIN t_call tc2 ON tc2.user_id_to = tu.id AND tc2.type = 1 " +
            "LEFT JOIN t_call_bill tcb ON tcb.user_id_to = tu.id AND tcb.type = 0 " +
            "LEFT JOIN t_call_bill tcb2 ON tcb2.user_id_to = tu.id AND tcb2.type = 1 " +
            "LEFT JOIN t_message_bill tmb ON tmb.user_id_to = tu.id " +
            "LEFT JOIN t_gift_bill tgb ON tgb.user_id_to = tu.id " +
            "LEFT JOIN t_asset tas ON tas.user_id = tu.id " +
            "LEFT JOIN t_active ta ON ta.user_id = tu.id " +
            "WHERE tu.identity = 1 AND " +
            "(tu.digit_id LIKE CONCAT('%', ?1, '%') OR ?1 IS NULL) AND " +
            "(tu.nick LIKE CONCAT('%', ?2, '%') OR ?2 IS NULL) AND " +
            "(tu.phone LIKE CONCAT('%', ?3, '%') OR ?3 IS NULL) AND " +
            "(tu.gender LIKE CONCAT('%', ?4, '%') OR ?4 IS NULL) AND " +
            "(tu.create_time BETWEEN ?5 AND ?6 OR ?5 IS NULL OR ?6 IS NULL) " +
            "GROUP BY tu.digit_id ORDER BY tu.digit_id DESC",
            nativeQuery = true)
    Page<AnchorDto> anchorStatistics(String digitId, String nick, String phone, Integer gender, Date startTime, Date endTime, Pageable pageable);

    // 用户数据模糊查询
    @Query(value = "SELECT tu.digit_id AS digitId, tu.nick AS nick, tu.phone AS phone, " +
            "tu.create_time AS createTime, " +
            "SUM(tr.coin) AS rechargeCoin, " +
            "SUM(tcb.origin_coin) AS audioCoin, " +
            "SUM(tcb2.origin_coin) AS videoCoin, " +
            "SUM(tmb.origin_coin) AS messageCoin, " +
            "SUM(tgb.origin_coin) AS giftCoin, " +
            "tas.coin As coin, " +
            "MAX(ta.active_time) AS activeTime " +
            "FROM t_user tu " +
            "LEFT JOIN t_recharge tr ON tr.user_id = tu.id AND tr.status >= 20 " +
            "LEFT JOIN t_call_bill tcb ON tcb.user_id_from = tu.id AND tcb.type = 0 " +
            "LEFT JOIN t_call_bill tcb2 ON tcb2.user_id_from = tu.id AND tcb.type = 1 " +
            "LEFT JOIN t_message_bill tmb ON tmb.user_id_from = tu.id " +
            "LEFT JOIN t_gift_bill tgb ON tgb.user_id_from = tu.id " +
            "LEFT JOIN t_asset tas ON tas.user_id = tu.id " +
            "LEFT JOIN t_active ta ON ta.user_id = tu.id " +
            "WHERE tu.identity = 0 AND " +
            "(tu.digit_id LIKE CONCAT('%', ?1, '%') OR ?1 IS NULL) AND " +
            "(tu.nick LIKE CONCAT('%', ?2, '%') OR ?2 IS NULL) AND " +
            "(tu.phone LIKE CONCAT('%', ?3, '%') OR ?3 IS NULL) AND " +
            "(tu.create_time BETWEEN ?4 AND ?5 OR ?4 IS NULL OR ?5 IS NULL) " +
            "GROUP BY tu.digit_id ORDER BY tu.digit_id DESC",
            nativeQuery = true)
    Page<ConsumerDto> consumerStatistics(String digitId, String nick, String phone, Date startTime, Date endTime, Pageable pageable);

}
