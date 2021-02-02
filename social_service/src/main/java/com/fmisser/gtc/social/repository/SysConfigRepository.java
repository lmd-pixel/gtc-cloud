package com.fmisser.gtc.social.repository;

import com.fmisser.gtc.social.domain.SysConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SysConfigRepository extends JpaRepository<SysConfig, Integer> {
    SysConfig findByName(String name);
}
