package com.fmisser.gtc.social.repository;

import com.fmisser.gtc.base.dto.social.ConcernDto;
import com.fmisser.gtc.base.dto.social.FansDto;
import com.fmisser.gtc.base.dto.social.FollowDto;
import com.fmisser.gtc.social.domain.Follow;
import com.fmisser.gtc.social.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {
    @Query(value = "SELECT user_id_from FROM t_follow WHERE user_id_to = ? AND status = 1 ORDER BY create_time DESC", nativeQuery = true)
    List<FollowDto> getFollowsTo(Long userId);

    @Query(value = "SELECT user_id_to FROM t_follow WHERE user_id_from = ? AND status = 1 ORDER BY create_time DESC", nativeQuery = true)
    List<FollowDto> getFollowsFrom(Long userId);

    Follow findByUserIdFromAndUserIdTo(Long userIdFrom, Long userIdTo);

    // 关注列表
    @Query(value = "SELECT new com.fmisser.gtc.base.dto.social.ConcernDto" +
            "(tu.digitId, tu.nick, tu.gender, tu.birth, tu.head, COUNT (tf2.id)) FROM Follow tf " +
            "INNER JOIN User tu ON tu.id = tf.userIdTo " +
            "LEFT JOIN Follow tf2 ON tf2.userIdFrom = tu.id AND tf2.userIdTo = tf.userIdFrom AND tf2.status = 1 " +
            "WHERE tf.status = 1 AND tf.userIdFrom = :userId " +
            "GROUP BY tf.id ORDER BY tf.modifyTime DESC")
    Page<ConcernDto> getConcernList(@Param("userId") Long userId, Pageable pageable);

    // 粉丝列表
    @Query(value = "SELECT new com.fmisser.gtc.base.dto.social.FansDto" +
            "(tu.digitId, tu.nick, tu.gender, tu.birth, tu.head, COUNT (tf2.id)) FROM Follow tf " +
            "INNER JOIN User tu ON tu.id = tf.userIdFrom " +
            "LEFT JOIN Follow tf2 ON tf2.userIdFrom = :userId AND tf2.userIdTo = tu.id AND tf2.status = 1 " +
            "WHERE tf.status = 1 AND tf.userIdTo = :userId " +
            "GROUP BY tf.id ORDER BY tf.modifyTime DESC")
    Page<FansDto> getFansList(@Param("userId") Long userId, Pageable pageable);

    @Query(value = "SELECT new com.fmisser.gtc.base.dto.social.FollowDto" +
            "(tf.userIdFrom, tf.userIdTo, tf.createTime,tf.status) " +
            "FROM Follow tf " +
            "WHERE tf.status=1 and tf.userIdTo = :userId and tf.userIdFrom =:selfUser " +
            "GROUP BY tf.id ORDER BY tf.modifyTime DESC")
    List<FollowDto> getFollwList(@Param("userId") Long userId,@Param("selfUser") Long selfUser);
}
