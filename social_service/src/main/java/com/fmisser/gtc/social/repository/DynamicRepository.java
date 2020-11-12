package com.fmisser.gtc.social.repository;

import com.fmisser.gtc.social.domain.Dynamic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DynamicRepository extends JpaRepository<Dynamic, Long> {
    Page<Dynamic> findByUserIdAndIsDeleteOrderByCreateTimeDesc(Long userId, int isDelete, Pageable pageable);
}
