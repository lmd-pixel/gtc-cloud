package com.fmisser.gtc.social.repository;

import com.fmisser.gtc.base.dto.social.AnchorDto;
import com.fmisser.gtc.base.dto.social.ConsumerDto;
import com.fmisser.gtc.base.dto.social.calc.CalcConsumeDto;
import com.fmisser.gtc.social.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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

    // 注意 要开启事务, service 开启了的话这里不用开启
//    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE t_user set follows = follows + 1 WHERE id = ?1", nativeQuery = true)
    int addUserFollow(Long userId);

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE t_user set follows = follows + 1 WHERE id = ?1", nativeQuery = true)
    int subUserFollow(Long userId);

    // 获取主播列表,根据创建时间排序
    @Query(value = "SELECT * FROM t_user " +
            "WHERE identity = 1 " +
            "AND (gender LIKE CONCAT('%', ?1, '%') OR ?1 IS NULL) " +
            "ORDER BY create_time DESC ",
            countQuery = "SELECT COUNT(*) FROM t_user " +
                    "WHERE identity = 1 " +
                    "AND (gender LIKE CONCAT('%', ?1, '%') OR ?1 IS NULL)",
            nativeQuery = true)
    Page<User> getAnchorListByCreateTime(Integer gender, Pageable pageable);

    // 获取主播列表，根据总收益排序
    @Query(value = "SELECT tu.*, " +
            "IFNULL(SUM(tmb.profit_coin), 0) + " +
            "IFNULL(SUM(tgb.profit_coin), 0) + " +
            "IFNULL(SUM(tcb.profit_coin), 0) AS profit " +
            "FROM t_user tu " +
            "LEFT JOIN t_message_bill tmb ON tu.id = tmb.user_id_to " +
            "LEFT JOIN t_gift_bill tgb ON tu.id = tgb.user_id_to " +
            "LEFT JOIN t_call_bill tcb ON tu.id = tcb.user_id_to " +
            "WHERE identity = 1 AND (gender LIKE CONCAT('%', ?1, '%') OR ?1 IS NULL) " +
            "GROUP BY tu.id ORDER BY profit DESC ",
    countQuery = "SELECT COUNT(*) FROM t_user " +
            "WHERE identity = 1 AND (gender LIKE CONCAT('%', ?1, '%') OR ?1 IS NULL) " ,
    nativeQuery = true)
    Page<User> getAnchorListByProfit(Integer gender, Pageable pageable);

    // 获取主播列表，根据系统推荐
    @Query(value = "SELECT tu.* " +
            "FROM t_user tu " +
            "INNER JOIN t_recommend tr ON tr.type = 0 AND tr.recommend = 1 AND tr.user_id = tu.id " +
            "WHERE identity = 1 AND (gender LIKE CONCAT('%', ?1, '%') OR ?1 IS NULL) " +
            "ORDER BY tr.level",
            countQuery = "SELECT COUNT(*) " +
            "FROM t_user tu " +
            "INNER JOIN t_recommend tr ON tr.type = 0 AND tr.recommend = 1 AND tr.user_id = tu.id " +
            "WHERE identity = 1 AND (gender LIKE CONCAT('%', ?1, '%') OR ?1 IS NULL) ",
            nativeQuery = true)
    Page<User> getAnchorListBySystem(Integer gender, Pageable pageable);

    // 获取主播列表，根据关注排序
    @Query(value = "SELECT * FROM t_user " +
            "WHERE identity = 1 " +
            "AND (gender LIKE CONCAT('%', ?1, '%') OR ?1 IS NULL) " +
            "ORDER BY follows DESC ",
            countQuery = "SELECT COUNT(*) FROM t_user " +
                    "WHERE identity = 1 " +
                    "AND (gender LIKE CONCAT('%', ?1, '%') OR ?1 IS NULL)",
            nativeQuery = true)
    Page<User> getAnchorListByFollow(Integer gender, Pageable pageable);

    // 根据推荐+关注排序
    @Query(value = "(SELECT tu.*, 1 AS sort1, tu.follows AS sort2 FROM t_user tu " +
            "INNER JOIN t_recommend tr ON tr.type = 0 AND tr.recommend = 1 AND tr.user_id = tu.id " +
            "WHERE identity = 1 AND (gender LIKE CONCAT('%', ?1, '%') OR ?1 IS NULL) " +
            "ORDER BY tr.level) UNION ALL " +
            "(SELECT tu2.*, 0 AS sort1, tu2.follows AS sort2 FROM t_user tu2 " +
            "LEFT JOIN t_recommend tr2 ON tr2.type = 0 AND tr2.recommend = 1 AND tr2.user_id = tu2.id " +
            "WHERE identity = 1 AND (gender LIKE CONCAT('%', ?1, '%') OR ?1 IS NULL) AND tr2.id IS NULL) " +
            "ORDER BY sort1 DESC , sort2 DESC",
            countQuery = "SELECT COUNT(*) FROM t_user " +
                    "WHERE identity = 1 " +
                    "AND (gender LIKE CONCAT('%', ?1, '%') OR ?1 IS NULL)",
            nativeQuery = true)
    Page<User> getAnchorListBySystemAndFollow(Integer gender, Pageable pageable);

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
            countQuery = "SELECT COUNT(tu.id) " +
                    "FROM t_user tu " +
                    "WHERE tu.identity = 1 AND " +
                    "(tu.digit_id LIKE CONCAT('%', ?1, '%') OR ?1 IS NULL) AND " +
                    "(tu.nick LIKE CONCAT('%', ?2, '%') OR ?2 IS NULL) AND " +
                    "(tu.phone LIKE CONCAT('%', ?3, '%') OR ?3 IS NULL) AND " +
                    "(tu.gender LIKE CONCAT('%', ?4, '%') OR ?4 IS NULL) AND " +
                    "(tu.create_time BETWEEN ?5 AND ?6 OR ?5 IS NULL OR ?6 IS NULL) ",
            nativeQuery = true)
    Page<AnchorDto> anchorStatistics(String digitId, String nick, String phone, Integer gender,
                                     Date startTime, Date endTime, Pageable pageable);

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
            "GROUP BY tu.digit_id ORDER BY tu.digit_id DESC ",
            countQuery = "SELECT COUNT(tu.digit_id) " +
                    "FROM t_user tu " +
                    "WHERE tu.identity = 0 AND " +
                    "(tu.digit_id LIKE CONCAT('%', ?1, '%') OR ?1 IS NULL) AND " +
                    "(tu.nick LIKE CONCAT('%', ?2, '%') OR ?2 IS NULL) AND " +
                    "(tu.phone LIKE CONCAT('%', ?3, '%') OR ?3 IS NULL) AND " +
                    "(tu.create_time BETWEEN ?4 AND ?5 OR ?4 IS NULL OR ?5 IS NULL) ",
            nativeQuery = true)
    Page<ConsumerDto> consumerStatistics(String digitId, String nick, String phone, Date startTime, Date endTime, Pageable pageable);
    // 目前不知道如何使用参数化order by desc/asc的办法，笨方法可以写两条sql
//    List<ConsumerDto> consumerStatistics(String digitId, String nick, String phone,
//                                         Date startTime, Date endTime,
//                                         String sort, int direction,
//                                         int limit, int offset);

    // 用户消费充值统计
    @Query(value = "SELECT COUNT(DISTINCT tu.id) AS count," +
            "SUM(tr.coin) AS recharge " +
//            "SUM(tcb.origin_coin) AS voiceConsume, " +
//            "SUM(tcb2.origin_coin) AS videoConsume, " +
//            "SUM(tmb.origin_coin) AS msgConsume, " +
//            "SUM(tgb.origin_coin) AS giftConsume " +
            "FROM t_user tu " +
            "LEFT JOIN t_recharge tr ON tr.user_id = tu.id AND tr.status >= 20 " +
//            "LEFT JOIN t_call_bill tcb ON tcb.user_id_from = tu.id AND tcb.type = 0 " +
//            "LEFT JOIN t_call_bill tcb2 ON tcb2.user_id_from = tu.id AND tcb.type = 1 " +
//            "LEFT JOIN t_message_bill tmb ON tmb.user_id_from = tu.id " +
//            "LEFT JOIN t_gift_bill tgb ON tgb.user_id_from = tu.id " +
            "WHERE tu.identity = 0 AND " +
            "(tu.digit_id LIKE CONCAT('%', ?1, '%') OR ?1 IS NULL) AND " +
            "(tu.nick LIKE CONCAT('%', ?2, '%') OR ?2 IS NULL) AND " +
            "(tu.phone LIKE CONCAT('%', ?3, '%') OR ?3 IS NULL) AND " +
            "(tu.create_time BETWEEN ?4 AND ?5 OR ?4 IS NULL OR ?5 IS NULL) ",
            nativeQuery = true)
    CalcConsumeDto calcConsume(String digitId, String nick, String phone, Date startTime, Date endTime);


}
