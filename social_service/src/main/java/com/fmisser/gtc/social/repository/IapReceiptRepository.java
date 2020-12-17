package com.fmisser.gtc.social.repository;

import com.fmisser.gtc.social.domain.IapReceipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;

@Repository
public interface IapReceiptRepository extends JpaRepository<IapReceipt, Long> {
    IapReceipt findByTransactionId(String transactionId);

    // FIXME: 2020/12/17 原生sql加 for update 方式和 lock注解是否一样
//    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    @Query(value = "SELECT * FROM t_iap_receipt WHERE transaction_id = ?1 FOR UPDATE", nativeQuery = true)
    IapReceipt getByTransactionIdWithLock(String transactionId);
}
