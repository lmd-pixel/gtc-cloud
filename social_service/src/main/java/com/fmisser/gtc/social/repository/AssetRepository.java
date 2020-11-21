package com.fmisser.gtc.social.repository;

import com.fmisser.gtc.social.domain.Asset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssetRepository extends JpaRepository<Asset, Long> {
    Asset findByUserId(Long userId);
}
