package com.fmisser.gtc.social.repository;

import com.fmisser.gtc.social.domain.Spread;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpreadRepository extends JpaRepository<Spread, Long> {
}
