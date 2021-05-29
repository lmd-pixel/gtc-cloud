package com.fmisser.gtc.social.repository;

import com.fmisser.gtc.base.dto.social.GuardDto;
import com.fmisser.gtc.social.domain.Guard;
import com.fmisser.gtc.social.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @author by fmisser
 * @create 2021/5/28 10:52 上午
 * @description TODO
 */
@Repository
public interface GuardRepository extends JpaRepository<Guard, Long> {

    // 获取主播的守护者
    @Query(value = "", nativeQuery = true)
    List<GuardDto> getAnchorGuardList(Long userId);

    // 获取用户守护的主播
    @Query(value = "", nativeQuery = true)
    List<GuardDto> getUserGuardList(Long userId);

    Optional<Guard> findByUserIdFromAndUserIdToAndIsDelete(Long userIdFrom, Long userIdTo, Integer isDelete);
}
