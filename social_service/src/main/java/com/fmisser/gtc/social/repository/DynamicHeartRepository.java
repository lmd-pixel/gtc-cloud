package com.fmisser.gtc.social.repository;

import com.fmisser.gtc.social.domain.DynamicHeart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DynamicHeartRepository extends JpaRepository<DynamicHeart, Long> {
    Optional<DynamicHeart> findByDynamicIdAndUserId(Long dynamicId, Long userId);
    List<DynamicHeart> findByDynamicIdAndUserIdAndIsCancel(Long dynamicId, Long userId, int isCancel);
}
