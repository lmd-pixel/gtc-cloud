package com.fmisser.gtc.social.repository;

import com.fmisser.gtc.social.domain.IapReceipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IapReceiptRepository extends JpaRepository<IapReceipt, Long> {

}
