package com.fmisser.gtc.social.repository;

import com.fmisser.gtc.base.dto.social.DynamicDto;
import com.fmisser.gtc.social.domain.Dynamic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DynamicRepository extends JpaRepository<Dynamic, Long> {
    Page<Dynamic> findByUserIdAndIsDeleteOrderByCreateTimeDesc(Long userId, int isDelete, Pageable pageable);

    Page<Dynamic> findByIsDeleteOrderByCreateTimeDesc(int isDelete, Pageable pageable);

//    @Query(value = "SELECT * FROM t_dynamic td INNER JOIN t_dynamic_heart tdh WHERE td.user_id = ?1 AND td.id = tdh.dynamic_id AND tdh.user_id = ?2 AND tdh.is_cancel = 0", nativeQuery = true)
//    Page<Dynamic> findDynamicList(Long userId, Long selfUserId, Pageable pageable);

    // 获取某个人的动态列表
    @Query(value = "SELECT new com.fmisser.gtc.base.dto.social.DynamicDto" +
            "(td.id, td.userId, tu.digitId, td.content, td.type, td.video, td.pictures, " +
            "td.createTime, td.modifyTime, td.longitude, td.latitude, " +
            "COUNT(DISTINCT tdh.id), COUNT(DISTINCT tdh2.id), COUNT(DISTINCT tdc.id), COUNT(DISTINCT tf.id), " +
            "tu.nick, tu.birth, tu.gender, tu.head) FROM Dynamic td " +
            "INNER JOIN User tu ON tu.id = td.userId " +
            "LEFT JOIN DynamicHeart tdh ON tdh.dynamicId = td.id AND tdh.isCancel = 0 " +
            "LEFT JOIN DynamicHeart tdh2 ON tdh2.dynamicId = td.id AND tdh2.isCancel = 0 AND tdh2.userId = :selfUserId " +
            "LEFT JOIN DynamicComment tdc ON tdc.dynamicId = td.id AND tdc.isDelete = 0 " +
            "LEFT JOIN Follow tf ON tf.userIdFrom = :selfUserId AND tf.userIdTo = td.userId AND tf.status = 1 " +
            "WHERE td.isDelete = 0 AND td.userId = :userId GROUP BY td.id " +
            "ORDER BY td.id DESC")
    Page<DynamicDto> getUserDynamicList(@Param("userId") Long userId, @Param("selfUserId") Long selfUserId, Pageable pageable);

    // 获取最新的动态列表
    @Query(value = "SELECT new com.fmisser.gtc.base.dto.social.DynamicDto" +
            "(td.id, td.userId, tu.digitId, td.content, td.type, td.video, td.pictures, " +
            "td.createTime, td.modifyTime, td.longitude, td.latitude, " +
            "COUNT(DISTINCT tdh.id), COUNT(DISTINCT tdh2.id), COUNT(DISTINCT tdc.id), COUNT(DISTINCT tf.id), " +
            "tu.nick, tu.birth, tu.gender, tu.head) FROM Dynamic td " +
            "INNER JOIN User tu ON tu.id = td.userId " +
            "LEFT JOIN DynamicHeart tdh ON tdh.dynamicId = td.id AND tdh.isCancel = 0 " +
            "LEFT JOIN DynamicHeart tdh2 ON tdh2.dynamicId = td.id AND tdh2.isCancel = 0 AND tdh2.userId = :selfUserId " +
            "LEFT JOIN DynamicComment tdc ON tdc.dynamicId = td.id AND tdc.isDelete = 0 " +
            "LEFT JOIN Follow tf ON tf.userIdFrom = :selfUserId AND tf.userIdTo = td.userId AND tf.status = 1 " +
            "WHERE td.isDelete = 0 GROUP BY td.id " +
            "ORDER BY td.id DESC")
    Page<DynamicDto> getLatestDynamicList(@Param("selfUserId") Long selfUserId, Pageable pageable);

    // 获取关注的人的动态列表
    @Query(value = "SELECT new com.fmisser.gtc.base.dto.social.DynamicDto" +
            "(td.id, td.userId, tu.digitId, td.content, td.type, td.video, td.pictures, " +
            "td.createTime, td.modifyTime, td.longitude, td.latitude, " +
            "COUNT(DISTINCT tdh.id), COUNT(DISTINCT tdh2.id), COUNT(DISTINCT tdc.id), " +
            "tu.nick, tu.birth, tu.gender, tu.head) FROM Follow tf " +
            "INNER JOIN Dynamic td ON td.userId = tf.userIdTo AND td.isDelete = 0 " +
            "INNER JOIN User tu ON tu.id = tf.userIdTo " +
            "LEFT JOIN DynamicHeart tdh ON tdh.dynamicId = td.id AND tdh.isCancel = 0 " +
            "LEFT JOIN DynamicHeart tdh2 ON tdh2.dynamicId = td.id AND tdh2.isCancel = 0 AND tdh2.userId = tf.userIdFrom " +
            "LEFT JOIN DynamicComment tdc ON tdc.dynamicId = td.id AND tdc.isDelete = 0 " +
            "WHERE tf.status = 1 AND tf.userIdFrom = :selfUserId GROUP BY td.id " +
            "ORDER BY td.id DESC")
    Page<DynamicDto> getDynamicListByFollow(@Param("selfUserId") Long selfUserId, Pageable pageable);
}
