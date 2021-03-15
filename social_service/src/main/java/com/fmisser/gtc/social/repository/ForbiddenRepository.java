package com.fmisser.gtc.social.repository;

import com.fmisser.gtc.base.dto.social.ForbiddenDto;
import com.fmisser.gtc.social.domain.Forbidden;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

public interface ForbiddenRepository extends JpaRepository<Forbidden, Long> {
    @Query(value = "SELECT * FROM t_forbidden " +
            "where user_id = ?1 AND disable = 0 AND " +
            "(days = 0 OR (start_time IS NOT NULL AND end_time IS NOT NULL AND NOW() BETWEEN start_time AND end_time))"
            , nativeQuery = true)
    Forbidden getCurrentForbidden(Long userId);

    List<Forbidden> findByUserIdAndDisable(Long userId, int disable);

    @Query(value = "SELECT tf.id AS id, tu.digit_id AS digitId, tu.nick AS nick, tu.identity AS identity, " +
            "tf.days AS days, tf.message AS message, tf.start_time AS startTime, tf.end_time AS endTime " +
            "FROM t_forbidden tf " +
            "INNER JOIN t_user tu ON tu.id = tf.user_id AND " +
            "(tu.digit_id LIKE CONCAT('%', ?1, '%') OR ?1 IS NULL) AND " +
            "(tu.nick LIKE CONCAT('%', ?2, '%') OR ?2 IS NULL) AND " +
            "(tu.identity LIKE CONCAT('%', ?3, '%') OR ?3 IS NULL ) " +
            "WHERE tf.disable = 0 AND " +
            "(days = 0 OR (start_time IS NOT NULL AND end_time IS NOT NULL AND NOW() BETWEEN start_time AND end_time))",
            countQuery = "SELECT COUNT(*) FROM t_forbidden tf " +
                    "INNER JOIN t_user tu ON tu.id = tf.user_id AND " +
                    "(tu.digit_id LIKE CONCAT('%', ?1, '%') OR ?1 IS NULL) AND " +
                    "(tu.nick LIKE CONCAT('%', ?2, '%') OR ?2 IS NULL) AND " +
                    "(tu.identity LIKE CONCAT('%', ?3, '%') OR ?3 IS NULL ) " +
                    "WHERE tf.disable = 0 AND " +
                    "(days = 0 OR (start_time IS NOT NULL AND end_time IS NOT NULL AND NOW() BETWEEN start_time AND end_time))",
            nativeQuery = true)
    Page<ForbiddenDto> getForbiddenList(String digitId, String nick, Integer identity, Pageable pageable);

    @Query(value = "SELECT tf.id AS id, tu.digit_id AS digitId, tu.nick AS nick, tu.identity AS identity, " +
            "tf.days AS days, tf.message AS message, tf.start_time AS startTime, tf.end_time AS endTime " +
            "FROM t_forbidden tf " +
            "INNER JOIN t_user tu ON tu.id = tf.user_id AND " +
            "(tu.digit_id LIKE CONCAT('%', ?1, '%') OR ?1 IS NULL) AND " +
            "(tu.nick LIKE CONCAT('%', ?2, '%') OR ?2 IS NULL) AND " +
            "(tu.identity LIKE CONCAT('%', ?3, '%') OR ?3 IS NULL ) " +
            "WHERE tf.disable = 0 AND " +
            "(days = 0 OR (start_time IS NOT NULL AND end_time IS NOT NULL AND ?4 BETWEEN start_time AND end_time))",
            countQuery = "SELECT COUNT(*) FROM t_forbidden tf " +
                    "INNER JOIN t_user tu ON tu.id = tf.user_id AND " +
                    "(tu.digit_id LIKE CONCAT('%', ?1, '%') OR ?1 IS NULL) AND " +
                    "(tu.nick LIKE CONCAT('%', ?2, '%') OR ?2 IS NULL) AND " +
                    "(tu.identity LIKE CONCAT('%', ?3, '%') OR ?3 IS NULL ) " +
                    "WHERE tf.disable = 0 AND " +
                    "(days = 0 OR (start_time IS NOT NULL AND end_time IS NOT NULL AND ?4 BETWEEN start_time AND end_time))",
            nativeQuery = true)
    Page<ForbiddenDto> getForbiddenListV2(String digitId, String nick, Integer identity, Date time, Pageable pageable);
}
