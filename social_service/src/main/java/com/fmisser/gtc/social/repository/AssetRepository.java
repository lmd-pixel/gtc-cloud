package com.fmisser.gtc.social.repository;

import com.fmisser.gtc.social.domain.Asset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import java.math.BigDecimal;

@Repository
public interface AssetRepository extends JpaRepository<Asset, Long> {
    Asset findByUserId(Long userId);

    // 通过加锁的方式锁定该数据直到事务提交，可有效避免当前其他事务修改数据
//    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
//    Asset findByUserId(Long userId);

    // 加金币,
    // 加减金币请使用下面两个函数操作，请勿在事务中先获取当前用户的金币，然后加减充值的金币，最后save到数据库
    // 如果在获取当前金币后其他地方修改了金币，这时候再加减充值的金币，此时数据则错误了
    // 除了使用以下两个update set 函数方法外
    // 还可以在获取金币的函数上通过加锁: @Lock(value = LockModeType.PESSIMISTIC_WRITE) 这将锁定该数据直到事务提交
    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE t_asset set coin = coin + ?2 WHERE user_id = ?1", nativeQuery = true)
    int addCoin(Long userId, BigDecimal coin);

    // 减金币
    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE t_asset set coin = coin - ?2 WHERE user_id = ?1", nativeQuery = true)
    int subCoin(Long userId, BigDecimal coin);
}
