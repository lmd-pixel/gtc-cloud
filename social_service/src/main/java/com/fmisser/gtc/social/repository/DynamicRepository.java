package com.fmisser.gtc.social.repository;

import com.fmisser.gtc.social.domain.Dynamic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DynamicRepository extends JpaRepository<Dynamic, Long> {
    Page<Dynamic> findByUserIdAndIsDeleteOrderByCreateTimeDesc(Long userId, int isDelete, Pageable pageable);

//    @Query(value = "SELECT * FROM t_dynamic td INNER JOIN t_dynamic_heart tdh WHERE td.user_id = ?1 AND td.id = tdh.dynamic_id AND tdh.user_id = ?2 AND tdh.is_cancel = 0", nativeQuery = true)
//    Page<Dynamic> findDynamicList(Long userId, Long selfUserId, Pageable pageable);
}
