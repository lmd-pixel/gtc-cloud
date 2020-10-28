package com.fmisser.gtc.social.repository;

import com.fmisser.gtc.social.domain.Follow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {
    @Query(value = "SELECT young_id_from FROM t_follow WHERE young_id_to = ?", nativeQuery = true)
    List<Long> getFollows(Long yongId);
}
