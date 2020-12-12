package com.fmisser.gtc.auth.repository;

import com.fmisser.gtc.auth.domain.PhoneCodeRequest;
import com.fmisser.gtc.base.dto.auth.PhoneCodeRequestDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhoneCodeRequestRepository extends JpaRepository<PhoneCodeRequest, Long> {
    PhoneCodeRequestDto findTopByPhoneAndTypeOrderByCreateTimeDesc(String phone, int type);
}
