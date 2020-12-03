package com.fmisser.gtc.social.repository;

import com.fmisser.gtc.base.dto.social.RecommendDto;
import com.fmisser.gtc.social.domain.Recommend;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface RecommendRepository extends JpaRepository<Recommend, Long> {

    @Query(value = "SELECT tr.type AS type, tr.level AS level, " +
            "tu.digit_id AS digitId, tu.nick AS nick, tu.phone AS phone, tu.gender AS gender " +
            "FROM t_recommend tr " +
            "INNER JOIN t_user tu ON tu.id = tr.user_id AND " +
            "(tu.digit_id LIKE CONCAT('%', ?1, '%') OR ?1 IS NULL) AND " +
            "(tu.nick LIKE CONCAT('%', ?2, '%') OR ?2 IS NULL) " +
            "WHERE tr.recommend = 1 AND tr.type = ?3 " +
            "ORDER BY tr.level ", nativeQuery = true)
    Page<RecommendDto> getRecommendList(String digitId, String nick, Integer type, Pageable pageable);

    Optional<Recommend> findByUserIdAndType(Long userId, int type);
}
