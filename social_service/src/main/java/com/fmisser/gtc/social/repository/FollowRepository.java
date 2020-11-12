package com.fmisser.gtc.social.repository;

import com.fmisser.gtc.base.dto.social.FollowDto;
import com.fmisser.gtc.social.domain.Follow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {
    @Query(value = "SELECT user_id_from FROM t_follow WHERE user_id_to = ? AND status = 1 ORDER BY create_time DESC", nativeQuery = true)
    List<FollowDto> getFollowsTo(Long userId);

    @Query(value = "SELECT user_id_to FROM t_follow WHERE user_id_from = ? AND status = 1 ORDER BY create_time DESC", nativeQuery = true)
    List<FollowDto> getFollowsFrom(Long userId);

    Follow findByUserIdFromAndUserIdTo(Long userIdFrom, Long userIdTo);
}
