package com.fmisser.gtc.auth.repository;

import com.fmisser.gtc.auth.domain.AppleAuthToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppleAuthTokenRepository extends JpaRepository<AppleAuthToken, Long> {
}
