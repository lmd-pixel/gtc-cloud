package com.fmisser.gtc.auth.repository;

import com.fmisser.gtc.auth.domain.AppleAuthKey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppleAuthKeyRepository extends JpaRepository<AppleAuthKey, Integer> {
    AppleAuthKey findByKid(String kid);
}
