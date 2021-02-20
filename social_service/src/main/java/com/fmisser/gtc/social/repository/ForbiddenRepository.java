package com.fmisser.gtc.social.repository;

import com.fmisser.gtc.social.domain.Forbidden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

public interface ForbiddenRepository extends JpaRepository<Forbidden, Long> {
    @Query(value = "SELECT * FROM t_forbidden " +
            "where user_id = ?1 AND disable = 0 AND " +
            "(days = 0 OR (start_time IS NOT NULL AND end_time IS NOT NULL AND ?2 BETWEEN start_time AND end_time))"
            , nativeQuery = true)
    Forbidden getCurrentForbidden(Long userId, Date date);

    List<Forbidden> findByUserIdAndDisable(Long userId, int disable);
}
