package com.fmisser.gtc.social.repository;

import com.fmisser.gtc.social.domain.Moderation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ModerationRepository extends JpaRepository<Moderation, Integer> {
    @Query(value = "SELECT * FROM t_moderation WHERE disable = 0", nativeQuery = true)
    List<Moderation> getModerationList();
}
