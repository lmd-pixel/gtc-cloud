package com.fmisser.gtc.social.repository;

import com.fmisser.gtc.social.domain.DynamicComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DynamicCommentRepository extends JpaRepository<DynamicComment, Long> {
    Page<DynamicComment> findByDynamicIdAndIsDeleteOrderByCreateTimeDesc(Long dynamicId, int isDelete, Pageable pageable);
}
