package com.fmisser.gtc.social.repository;

import com.fmisser.gtc.base.dto.social.DynamicDto;
import com.fmisser.gtc.social.domain.Dynamic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface DynamicRepository extends JpaRepository<Dynamic, Long> {
   

    Page<Dynamic> findByUserIdAndIsDeleteOrderByCreateTimeDesc(Long userId, int isDelete, Pageable pageable);

    Page<Dynamic> findByIsDeleteOrderByCreateTimeDesc(int isDelete, Pageable pageable);

//    @Query(value = "SELECT * FROM t_dynamic td INNER JOIN t_dynamic_heart tdh WHERE td.user_id = ?1 AND td.id = tdh.dynamic_id AND tdh.user_id = ?2 AND tdh.is_cancel = 0", nativeQuery = true)
//    Page<Dynamic> findDynamicList(Long userId, Long selfUserId, Pageable pageable);

    @Query(value = "SELECT COUNT(*) FROM t_dynamic WHERE user_id = ?1 AND is_delete = 0 AND create_time BETWEEN ?2 AND ?3", nativeQuery = true)
    Long countTodayDynamic(Long userId, Date startTime, Date endTime);

    // 获取某个人的动态列表
    @Query(value = "SELECT new com.fmisser.gtc.base.dto.social.DynamicDto" +
            "(td.id, td.userId, tu.digitId, tu.identity,tu.intro,td.content, td.city, td.type, td.video, td.pictures, " +
            "td.createTime, td.modifyTime, td.longitude, td.latitude, " +
            "COUNT(DISTINCT tdh.id), COUNT(DISTINCT tdh2.id), COUNT(DISTINCT tdc.id), COUNT(DISTINCT tf.id), " +
            "tu.nick, tu.birth, tu.gender, tu.head) FROM Dynamic td " +
            "INNER JOIN User tu ON tu.id = td.userId " +
            "LEFT JOIN Block tb ON tb.block = 1 AND tb.type = 10 AND tb.userId = :selfUserId AND tb.blockUserId = td.userId " +
            "LEFT JOIN Block tb2 ON tb2.block = 1 AND tb2.type = 12 AND tb2.userId = :selfUserId AND tb2.blockUserId = td.userId AND tb2.blockDynamicId = td.id " +
            "LEFT JOIN DynamicHeart tdh ON tdh.dynamicId = td.id AND tdh.isCancel = 0 " +
            "LEFT JOIN DynamicHeart tdh2 ON tdh2.dynamicId = td.id AND tdh2.isCancel = 0 AND tdh2.userId = :selfUserId " +
            "LEFT JOIN DynamicComment tdc ON tdc.dynamicId = td.id AND tdc.isDelete = 0 " +
            "LEFT JOIN Follow tf ON tf.userIdFrom = :selfUserId AND tf.userIdTo = td.userId AND tf.status = 1 " +
            "WHERE td.isDelete = 0 AND td.status IN :status AND td.userId = :userId AND " +
            "(:createTime IS NULL OR td.createTime < :createTime) AND " +
            "tb.id IS NULL AND tb2.id IS NULL GROUP BY td.id " +
            "ORDER BY td.id DESC")
    Page<DynamicDto> getUserDynamicList(@Param("userId") Long userId,
                                        @Param("selfUserId") Long selfUserId,
                                        @Param("status") List<Integer> status,
                                        @Param("createTime") Date createTime,
                                        Pageable pageable);

    // 获取最新的动态列表
    @Query(value = "SELECT new com.fmisser.gtc.base.dto.social.DynamicDto" +
            "(td.id, td.userId, tu.digitId, tu.identity,tu.intro,td.content, td.city, td.type, td.video, td.pictures, " +
            "td.createTime, td.modifyTime, td.longitude, td.latitude, " +
            "COUNT(DISTINCT tdh2.id), " +
            "tu.nick, tu.birth, tu.gender, tu.head) FROM Dynamic td " +
            "INNER JOIN User tu ON tu.id = td.userId " +
            "LEFT JOIN Block tb ON tb.block = 1 AND tb.userId = :selfUserId AND " +
            "((tb.type = 10 AND tb.blockUserId = td.userId) OR (tb.type = 12 AND tb.blockUserId = td.userId AND tb.blockDynamicId = td.id)) "+
//            "LEFT JOIN Block tb ON tb.block = 1 AND tb.type = 10 AND tb.userId = :selfUserId AND tb.blockUserId = td.userId " +
//            "LEFT JOIN Block tb2 ON tb2.block = 1 AND tb2.type = 12 AND tb2.userId = :selfUserId AND tb2.blockUserId = td.userId AND tb2.blockDynamicId = td.id " +
           // "LEFT JOIN DynamicHeart tdh ON tdh.dynamicId = td.id AND tdh.isCancel = 0 " +
            "LEFT JOIN DynamicHeart tdh2 ON tdh2.dynamicId = td.id AND tdh2.isCancel = 0 AND tdh2.userId = :selfUserId " +
          //  "LEFT JOIN DynamicComment tdc ON tdc.dynamicId = td.id AND tdc.isDelete = 0 " +
          //  "LEFT JOIN Follow tf ON tf.userIdFrom = :selfUserId AND tf.userIdTo = td.userId AND tf.status = 1 " +
            "WHERE td.isDelete = 0 AND td.status = 10 AND " +
            "(:createTime IS NULL OR td.createTime < :createTime) AND " +
            "tb.id IS NULL " +
//            "AND tb2.id IS NULL " +
            "GROUP BY td.id, td.modifyTime " +
            "ORDER BY td.modifyTime DESC")
    Page<DynamicDto> getLatestDynamicList(@Param("selfUserId") Long selfUserId,
                                          @Param("createTime") Date createTime,
                                          Pageable pageable);

    @Query(value = "SELECT td.id AS id, td.user_id AS userId, td.content AS content, td.city AS city, td.type AS type, " +
            "td.video AS video, td.pictures AS pictures, td.create_time AS createTime, td.modify_time AS modifyTime, " +
            "td.longitude AS longitude, td.latitude AS latitude, tu.nick AS nick, tu.birth AS birth, tu.gender AS gender, tu.head AS head, " +
            "(SELECT COUNT(DISTINCT tdh.id) FROM t_dynamic_heart tdh WHERE tdh.dynamic_id = td.id AND tdh.is_cancel = 0) AS heartCount, " +
            "(SELECT COUNT(DISTINCT tdh.id) FROM t_dynamic_heart tdh WHERE tdh.dynamic_id = td.id AND tdh.is_cancel = 0 AND tdh.user_id = ?1) AS selfHeart, " +
            "(SELECT COUNT(DISTINCT tdc.id) FROM t_dynamic_comment tdc WHERE tdc.dynamic_id = td.id AND tdc.is_delete) AS commentCount," +
            "(SELECT COUNT(DISTINCT tf.id) FROM t_follow tf WHERE tf.user_id_from = ?1 AND tf.user_id_to = td.user_id AND tf.status = 1) AS follow " +
//            "COUNT(DISTINCT tdh.id) AS heartCount, COUNT(DISTINCT tdh2.id) AS selfHeart, COUNT(DISTINCT tdc.id) AS commentCount, COUNT(DISTINCT tf.id) AS follow " +
            "FROM t_dynamic td " +
            "INNER JOIN t_user tu ON tu.id = td.user_id " +
            "LEFT JOIN t_block tb ON tb.block = 1 AND tb.user_id = ?1 AND " +
            "((tb.type = 10 AND tb.block_user_id = td.user_id) OR (tb.type = 12 AND tb.block_user_id = td.user_id AND tb.block_dynamic_id = td.id)) " +
//            "LEFT JOIN t_block tb ON tb.block = 1 AND tb.type = 10 AND tb.user_id = ?1 AND tb.block_user_id = td.user_id " +
//            "LEFT JOIN t_block tb2 ON tb2.block = 1 AND tb2.type = 12 AND tb2.user_id = ?1 AND tb2.block_user_id = td.user_id AND tb2.block_dynamic_id = td.id " +
//            "LEFT JOIN t_dynamic_heart tdh ON tdh.dynamic_id = td.id AND tdh.is_cancel = 0 " +
//            "LEFT JOIN t_dynamic_heart tdh2 ON tdh2.dynamic_id = td.id AND tdh2.is_cancel = 0 AND tdh2.user_id = ?1 " +
//            "LEFT JOIN t_dynamic_comment tdc ON tdc.dynamic_id = td.id AND tdc.is_delete = 0 " +
//            "LEFT JOIN t_follow tf ON tf.user_id_from = ?1 AND tf.user_id_to = td.user_id AND tf.status = 1 " +
            "WHERE td.is_delete = 0 AND td.status = 10 AND " +
            "(?2 IS NULL OR td.create_time < ?2) " +
            "AND tb.id IS NULL " +
            "GROUP BY td.id, td.modify_time " +
            "ORDER BY td.modify_time DESC",
            nativeQuery = true)
    Page<DynamicDto> getLatestDynamicListEx(Long selfUserId, Date createTime, Pageable pageable);

    // 获取关注的人的动态列表
    @Query(value = "SELECT new com.fmisser.gtc.base.dto.social.DynamicDto" +
            "(td.id, td.userId, tu.digitId, td.content, td.city, td.type, td.video, td.pictures, " +
            "td.createTime, td.modifyTime, td.longitude, td.latitude, " +
            "COUNT(DISTINCT tdh.id), COUNT(DISTINCT tdh2.id), COUNT(DISTINCT tdc.id), " +
            "tu.nick, tu.birth, tu.gender, tu.head) FROM Follow tf " +
            "INNER JOIN Dynamic td ON td.userId = tf.userIdTo AND td.isDelete = 0 AND td.status = 10 " +
            "INNER JOIN User tu ON tu.id = tf.userIdTo " +
            "LEFT JOIN Block tb ON tb.block = 1 AND tb.type = 10 AND tb.userId = :selfUserId AND tb.blockUserId = td.userId " +
            "LEFT JOIN Block tb2 ON tb2.block = 1 AND tb2.type = 12 AND tb2.userId = :selfUserId AND tb2.blockUserId = td.userId AND tb2.blockDynamicId = td.id " +
            "LEFT JOIN DynamicHeart tdh ON tdh.dynamicId = td.id AND tdh.isCancel = 0 " +
            "LEFT JOIN DynamicHeart tdh2 ON tdh2.dynamicId = td.id AND tdh2.isCancel = 0 AND tdh2.userId = tf.userIdFrom " +
            "LEFT JOIN DynamicComment tdc ON tdc.dynamicId = td.id AND tdc.isDelete = 0 " +
            "WHERE tf.status = 1 AND tf.userIdFrom = :selfUserId AND " +
            "(:createTime IS NULL OR td.createTime < :createTime) AND " +
            "tb.id IS NULL AND tb2.id IS NULL " +
            "GROUP BY td.id, td.modifyTime " +
            "ORDER BY td.modifyTime DESC")
    Page<DynamicDto> getDynamicListByFollow(@Param("selfUserId") Long selfUserId, @Param("createTime") Date createTime, Pageable pageable);


    // 查询动态列表（管理接口）
    @Query(value = "SELECT new com.fmisser.gtc.base.dto.social.DynamicDto" +
            "(td.id, td.userId, tu.digitId,tu.identity, td.content, td.city, td.type, td.video, td.pictures, " +
            "td.createTime, td.modifyTime, td.longitude, td.latitude, " +
            "COUNT(DISTINCT tdh.id), COUNT(DISTINCT tdc.id), " +
            "tu.nick, tu.birth, tu.gender, tu.head, td.message, td.status) FROM Dynamic td " +
            "INNER JOIN User tu ON tu.id = td.userId AND " +
            "(tu.digitId LIKE CONCAT('%', :digitId, '%') OR :digitId IS NULL ) AND " +
            "(tu.nick LIKE CONCAT('%', :nick, '%') OR :nick IS NULL ) " +
            "LEFT JOIN DynamicHeart tdh ON tdh.dynamicId = td.id AND tdh.isCancel = 0 " +
            "LEFT JOIN DynamicComment tdc ON tdc.dynamicId = td.id AND tdc.isDelete = 0 " +
            "WHERE td.isDelete = 0 AND td.status = 10 AND " +
//            "td.content LIKE CONCAT('%', :content, '%') OR :content IS NULL AND " +
            "(td.createTime BETWEEN :startTime AND :endTime OR :startTime IS NULL OR :endTime IS NULL)" +
            "GROUP BY td.id " +
            "ORDER BY td.id DESC")
    Page<DynamicDto> getManagerDynamicList(@Param("digitId") String digitId,
                                           @Param("nick") String nick,
//                                           @Param("content") String content,
                                           @Param("startTime") Date startTime,
                                           @Param("endTime") Date endTime,
                                           Pageable pageable);

    @Query(value = "SELECT new com.fmisser.gtc.base.dto.social.DynamicDto" +
            "(td.id, td.userId, tu.digitId,tu.identity, td.content, td.city, td.type, td.video, td.pictures, " +
            "td.createTime, td.modifyTime, td.longitude, td.latitude, " +
            "COUNT(DISTINCT tdh.id), COUNT(DISTINCT tdc.id), " +
            "tu.nick, tu.birth, tu.gender, tu.head, td.message, td.status) FROM Dynamic td " +
            "INNER JOIN User tu ON tu.id = td.userId AND " +
            "(tu.digitId LIKE CONCAT('%', :digitId, '%') OR :digitId IS NULL ) AND " +
            "(tu.nick LIKE CONCAT('%', :nick, '%') OR :nick IS NULL ) " +
            "LEFT JOIN DynamicHeart tdh ON tdh.dynamicId = td.id AND tdh.isCancel = 0 " +
            "LEFT JOIN DynamicComment tdc ON tdc.dynamicId = td.id AND tdc.isDelete = 0 " +
            "WHERE td.isDelete = 0 AND td.status IN (1,20) AND " +
//            "td.content LIKE CONCAT('%', :content, '%') OR :content IS NULL AND " +
            "(td.createTime BETWEEN :startTime AND :endTime OR :startTime IS NULL OR :endTime IS NULL)" +
            "GROUP BY td.id " +
            "ORDER BY td.id DESC")
    Page<DynamicDto> getManagerGuardDynamicList(@Param("digitId") String digitId,
                                                @Param("nick") String nick,
//                                                @Param("content") String content,
                                                @Param("startTime") Date startTime,
                                                @Param("endTime") Date endTime,
                                                Pageable pageable);
}
