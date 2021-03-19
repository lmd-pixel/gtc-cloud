package com.fmisser.gtc.social.repository;

import com.fmisser.gtc.social.domain.Invite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InviteRepository extends JpaRepository<Invite, Long> {
    Invite findByInvitedUserId(Long userId);
}
