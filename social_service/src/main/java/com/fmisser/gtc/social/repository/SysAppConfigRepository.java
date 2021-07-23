package com.fmisser.gtc.social.repository;

import com.fmisser.gtc.social.domain.SysAppConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface SysAppConfigRepository extends JpaRepository<SysAppConfig, Integer> {
    SysAppConfig findByName(String name);
}
