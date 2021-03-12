package com.fmisser.gtc.social.repository;

import com.fmisser.gtc.social.domain.Gift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GiftRepository extends JpaRepository<Gift, Long> {
    @Query(nativeQuery = true, value = "SELECT * FROM t_gift tg WHERE tg.disable = 0 ORDER BY tg.price, tg.level")
    List<Gift> getGiftList();
}
