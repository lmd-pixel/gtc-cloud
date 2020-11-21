package com.fmisser.gtc.social.repository;

import com.fmisser.gtc.social.domain.MessageBill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageBillRepository extends JpaRepository<MessageBill, Long> {
}
