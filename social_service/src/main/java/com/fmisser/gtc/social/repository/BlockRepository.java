package com.fmisser.gtc.social.repository;

import com.fmisser.gtc.base.dto.social.BlockDto;
import com.fmisser.gtc.social.domain.Block;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlockRepository extends JpaRepository<Block, Long> {
    Block findByUserIdAndTypeAndBlockUserId(Long userId, int type, Long blockUserId);
    List<Block> findByUserIdAndBlockUserIdAndTypeIsIn(Long userId, Long blockUserId, List<Integer> types);


    @Query(value = "SELECT new com.fmisser.gtc.base.dto.social.BlockDto" +
            "(tf.userId, tf.blockUserId, tf.blockDynamicId,tf.createTime) " +
            "FROM Block tf " +
            "WHERE tf.block=1  and tf.userId =:selfUser AND " +
            "(tf.type = 10 AND tf.blockUserId = :userId) OR (tf.type = 12 AND tf.blockUserId = :userId AND tf.blockDynamicId = :dynamicId) "+
            "GROUP BY tf.id ORDER BY tf.createTime DESC")
    List<BlockDto> getBlockList(@Param("dynamicId") Long dynamicId,@Param("userId") Long userId, @Param("selfUser") Long selfUser);
}
