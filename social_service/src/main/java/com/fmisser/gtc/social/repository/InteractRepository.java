package com.fmisser.gtc.social.repository;

import com.fmisser.gtc.social.domain.Interact;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InteractRepository extends JpaRepository<Interact, Long> {
    // 获取动态的评论列表
    Page<Interact> findByDynamicIdAndTypeAndIsDeleteOrderByCreateTimeDesc(Long dynamicId, int type, int isDelete, Pageable pageable);
    Optional<Interact> findByDynamicIdAndUserId(Long dynamicId, Long userId);
}
