package com.fmisser.gtc.social.repository;

import com.fmisser.gtc.base.dto.social.PayAuditDto;
import com.fmisser.gtc.base.dto.social.WithdrawAuditDto;
import com.fmisser.gtc.social.domain.WithdrawAudit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface WithdrawAuditRepository extends JpaRepository<WithdrawAudit, Long> {

    @Query(value = "SELECT * FROM t_withdraw_audit WHERE status = ?1 ORDER BY create_time DESC", nativeQuery = true)
    Page<WithdrawAudit> getAuditWithStatus(int status, Pageable pageable);

    Optional<WithdrawAudit> findByOrderNumber(String orderNumber);

    // 获取待审核列表
    @Query(value = "SELECT twa.order_number AS orderNumber, twa.draw_curr AS drawCurr, " +
            "twa.draw_max AS drawMax, twa.draw_actual As drawActual, " +
            "twa.fee_ratio AS feeRatio, twa.fee AS fee, " +
            "twa.coin_before AS coinBefore, twa.coin_after AS coinAfter, " +
            "twa.status, twa.remark AS remark, twa.create_time AS createTime, " +
            "tu.digit_id AS digitId, tu.nick AS nick, tu.phone AS phone " +
            "FROM t_withdraw_audit twa " +
            "INNER JOIN t_user tu ON tu.id = twa.user_id AND " +
            "(tu.digit_id LIKE CONCAT('%', ?1, '%') OR ?1 IS NULL) AND " +
            "(tu.nick LIKE CONCAT('%', ?2, '%') OR ?2 IS NULL) " +
            "WHERE twa.status = 10 AND " +
            "(twa.create_time BETWEEN ?3 AND ?4 OR ?3 IS NULL OR ?4 IS NULL) " +
            "ORDER BY twa.create_time DESC ", nativeQuery = true)
    Page<WithdrawAuditDto> getWithdrawAuditList(String digitId, String nick, Date startTime, Date endTime, Pageable pageable);

    // 获取提现列表
    @Query(value = "SELECT twa.order_number AS orderNumber, twa.draw_curr AS drawCurr, " +
            "twa.draw_max AS drawMax, twa.draw_actual As drawActual, " +
            "twa.fee_ratio AS feeRatio, twa.fee AS fee, " +
            "twa.coin_before AS coinBefore, twa.coin_after AS coinAfter, " +
            "twa.status, twa.remark AS remark, twa.create_time AS createTime, " +
            "tu.digit_id AS digitId, tu.nick AS nick, tu.phone AS phone " +
            "FROM t_withdraw_audit twa " +
            "INNER JOIN t_user tu ON tu.id = twa.user_id AND " +
            "(tu.digit_id LIKE CONCAT('%', ?1, '%') OR ?1 IS NULL) AND " +
            "(tu.nick LIKE CONCAT('%', ?2, '%') OR ?2 IS NULL) " +
            "WHERE twa.status IN (?5) AND " +
            "(twa.create_time BETWEEN ?3 AND ?4 OR ?3 IS NULL OR ?4 IS NULL) " +
            "ORDER BY twa.create_time DESC ", nativeQuery = true)
    Page<WithdrawAuditDto> getWithdrawList(String digitId, String nick, Date startTime, Date endTime, List<Integer> status, Pageable pageable);

    // 获取待打款列表
    @Query(value = "SELECT twa.order_number AS orderNumber, twa.pay_money AS payMoney, twa.pay_actual AS payActual, " +
            "twa.money_type As moneyType, twa.pay_type AS payType, twa.pay_to_people AS payToPeople, " +
            "twa.pay_to_account AS payToAccount, twa.remark AS remark, twa.create_time AS createTime, " +
            "tu.digit_id AS digitId, tu.nick AS nick, tu.phone AS phone " +
            "FROM t_withdraw_audit twa " +
            "INNER JOIN t_user tu ON tu.id = twa.user_id AND " +
            "(tu.digit_id LIKE CONCAT('%', ?1, '%') OR ?1 IS NULL) AND " +
            "(tu.nick LIKE CONCAT('%', ?2, '%') OR ?2 IS NULL) " +
            "WHERE twa.status = 30 AND " +
            "(twa.pay_type = ?3 OR ?3 IS NULL) AND " +
            "(twa.create_time BETWEEN ?4 AND ?5 OR ?4 IS NULL OR ?5 IS NULL) " +
            "ORDER BY twa.create_time DESC ", nativeQuery = true)
    Page<PayAuditDto> getPayAuditList(String digitId, String nick, Integer payType, Date startTime, Date endTime, Pageable pageable);

}
