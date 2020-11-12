package com.fmisser.gtc.social.repository;

import com.fmisser.gtc.social.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUsername(String username);
    long countByCreateTimeBetween(Date start, Date end);
    User findByUsername(String username);
}
