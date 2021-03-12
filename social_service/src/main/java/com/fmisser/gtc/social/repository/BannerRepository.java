package com.fmisser.gtc.social.repository;

import com.fmisser.gtc.social.domain.Banner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BannerRepository extends JpaRepository<Banner, Long> {
    List<Banner> findAllByLangAndDisable(String lang, int disable);
}
