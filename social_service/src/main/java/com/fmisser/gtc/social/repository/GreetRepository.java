package com.fmisser.gtc.social.repository;

import com.fmisser.gtc.social.domain.Greet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface GreetRepository extends JpaRepository<Greet, Long> {
    List<Greet> findByUserIdAndCreatTimeBetween(Long userId, Date startTime, Date endTime);

    List<Greet> findByUserIdAndCreatTimeBetweenAndReplyMsgIdNotNull(Long userId, Date startTime, Date endTime);

    List<Greet> findByUserIdAndCreatTimeBetweenAndStage(Long userId, Date startTime, Date endTime, Integer stage);

    Greet findByUserIdAndAnchorIdAndCreatTimeBetween(Long userId, Long anchorId, Date startTime, Date endTime);
}
