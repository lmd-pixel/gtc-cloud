package com.fmisser.gtc.social.repository;

import com.fmisser.gtc.social.domain.Moderation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ModerationRepository extends JpaRepository<Moderation, Integer> {
    @Query(value = "SELECT * FROM `gtc-social-db`.t_moderation WHERE disable = 0 and type = ?1", nativeQuery = true)
    List<Moderation> getModerationList(int type);
}
