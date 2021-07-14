package com.fmisser.gtc.social.repository;

import com.fmisser.gtc.social.domain.Friend;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author by fmisser
 * @create 2021/7/7 5:08 下午
 * @description TODO
 */
public interface FriendRepository extends JpaRepository<Friend, Long> {
}
