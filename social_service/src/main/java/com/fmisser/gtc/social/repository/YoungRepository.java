package com.fmisser.gtc.social.repository;

import com.fmisser.gtc.social.domain.Young;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface YoungRepository extends JpaRepository<Young, Long> {
    boolean existsByPhone(String phone);
}
