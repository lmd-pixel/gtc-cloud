package com.fmisser.gtc.social.repository;

import com.fmisser.gtc.social.domain.IdentityAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IdentityAuditRepository extends JpaRepository<IdentityAudit, Long> {
    Optional<IdentityAudit> findByUserIdAndTypeOrderByCreateTimeDesc(Long userId, int type);
}
