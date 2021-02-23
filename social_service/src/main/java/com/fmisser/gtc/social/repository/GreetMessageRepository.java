package com.fmisser.gtc.social.repository;

import com.fmisser.gtc.social.domain.GreetMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface GreetMessageRepository extends JpaRepository<GreetMessage, Integer> {

    // 获取随机问候语
    @Query(value = "SELECT * FROM t_greet_message WHERE disable = 0 AND type = ?2 ORDER BY RAND() LIMIT ?1", nativeQuery = true)
    List<GreetMessage> findRandGreetMessageList(int limit, int type);
}
