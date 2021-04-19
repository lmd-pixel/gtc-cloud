package com.fmisser.gtc.social.repository;

import com.fmisser.gtc.social.domain.Active;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ActiveRepository extends JpaRepository<Active, Long> {
    // 获取特定身份的活跃人数
    Long countDistinctByIdentityAndActiveTimeBetween(int identity, Date startTime, Date endTime);
    // 获取活跃人数
    Long countDistinctByActiveTimeBetween(Date startTime, Date endTime);

    // 一次查询活跃的普通用户和主播
    @Query(value = "SELECT COUNT(DISTINCT (user_id)) FROM t_active WHERE " +
            "active_time BETWEEN ?2 AND ?3 GROUP BY identity",nativeQuery = true)
    List<Long> countActiveOnce(Date startTime, Date endTime);

    Active findTopByUserIdAndStatusIn(Long userId, List<Integer> statusList);

    Active findTopByUserIdAndType(Long userId, int type);
}
