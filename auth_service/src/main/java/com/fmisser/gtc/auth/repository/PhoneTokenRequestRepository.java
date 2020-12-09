package com.fmisser.gtc.auth.repository;

import com.fmisser.gtc.auth.domain.PhoneTokenRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhoneTokenRequestRepository extends JpaRepository<PhoneTokenRequest, Long> {
}
