package com.fmisser.gtc.social.repository;

import com.fmisser.gtc.social.domain.UserMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserMessageRepository extends JpaRepository<UserMessage, Long> {

}
