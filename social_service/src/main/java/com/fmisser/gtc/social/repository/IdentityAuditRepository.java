package com.fmisser.gtc.social.repository;

import com.fmisser.gtc.social.domain.IdentityAudit;
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
public interface IdentityAuditRepository extends JpaRepository<IdentityAudit, Long> {
    // 获取最新的指定类型的审核
    Optional<IdentityAudit> findTopByUserIdAndTypeOrderByCreateTimeDesc(Long userId, int type);

    @Query(value = "(SELECT * FROM t_identity_audit WHERE user_id = ?1 AND type = 1 ORDER BY create_time DESC LIMIT 1) " +
            "UNION ALL (SELECT * FROM t_identity_audit WHERE user_id = ?1 AND type = 2 ORDER BY create_time DESC LIMIT 1) " +
            "UNION ALL (SELECT * FROM t_identity_audit WHERE user_id = ?1 AND type = 3 ORDER BY create_time DESC LIMIT 1)",
            nativeQuery = true)
    List<IdentityAudit> getLatestWithAllType(Long userId);

    // 通过审核编号获取
    IdentityAudit findBySerialNumber(String serialNumber);

    // 获取身份审核列表
    @Query(value = "SELECT * FROM t_identity_audit " +
            "WHERE (create_time BETWEEN ?5 AND ?6 OR ?5 IS NULL OR ?6 IS NULL ) AND " +
            "(digit_id LIKE CONCAT('%', ?1, '%') OR ?1 IS NULL) AND " +
            "(nick LIKE CONCAT('%', ?2, '%') OR ?2 IS NULL) AND " +
            "(gender LIKE CONCAT('%', ?3, '%') OR ?3 IS NULL) AND " +
            "(status IN (?4) ) " +
            "ORDER BY create_time DESC",
            countQuery = "SELECT COUNT(*) FROM t_identity_audit " +
                    "WHERE (create_time BETWEEN ?5 AND ?6 OR ?5 IS NULL OR ?6 IS NULL ) AND " +
                    "(digit_id LIKE CONCAT('%', ?1, '%') OR ?1 IS NULL) AND " +
                    "(nick LIKE CONCAT('%', ?2, '%') OR ?2 IS NULL) AND " +
                    "(gender LIKE CONCAT('%', ?3, '%') OR ?3 IS NULL) AND " +
                    "(status IN (?4) ) ",
            nativeQuery = true)
    Page<IdentityAudit> getIdentityAuditList(String digitId, String nick, Integer gender, List<Integer> status,
                                           Date startTime, Date endTime, Pageable pageable);
}
