package com.fmisser.gtc.social.repository;

import com.fmisser.gtc.social.domain.UserMaterial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @author by fmisser
 * @create 2021/5/26 4:39 下午
 * @description TODO
 */

@Repository
public interface UserMaterialRepository extends JpaRepository<UserMaterial, Long> {

    @Query(value = "SELECT * FROM t_user_material tum " +
            "WHERE tum.user_id = ?1 AND tum.type = ?2 AND tum.is_delete = 0",
            nativeQuery = true)
    List<UserMaterial> getPhotos(Long userId, Integer type);

    @Query(value = "SELECT * FROM t_user_material tum " +
            "WHERE tum.user_id = ?1 AND tum.type = 3 AND tum.is_delete = 0",
            nativeQuery = true)
    UserMaterial getAuditVideo(Long userId);
}
