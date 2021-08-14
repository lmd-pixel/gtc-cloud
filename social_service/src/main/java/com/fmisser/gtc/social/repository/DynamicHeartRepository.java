package com.fmisser.gtc.social.repository;

import com.fmisser.gtc.base.dto.social.DynamicHeartListDto;
import com.fmisser.gtc.social.domain.DynamicHeart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DynamicHeartRepository extends JpaRepository<DynamicHeart, Long> {
    Optional<DynamicHeart> findByDynamicIdAndUserId(Long dynamicId, Long userId);
    List<DynamicHeart> findByDynamicIdAndUserIdAndIsCancel(Long dynamicId, Long userId, int isCancel);


    @Query(value = "SELECT new com.fmisser.gtc.base.dto.social.DynamicHeartListDto" +
            "(tdc.id, tdc.dynamicId, tdc.userId, tdc.createTime, tdc.isCancel) " +
            "FROM DynamicHeart tdc " +
            "WHERE tdc.isCancel = 0  AND tdc.dynamicId = :dynamicId  GROUP BY tdc.id " +
            "ORDER BY tdc.id DESC")

    List<DynamicHeartListDto> getDynamicHeartList(Long dynamicId);





}
