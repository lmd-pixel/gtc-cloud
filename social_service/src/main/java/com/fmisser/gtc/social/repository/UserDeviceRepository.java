package com.fmisser.gtc.social.repository;

import com.fmisser.gtc.social.domain.UserDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author fmisser
 * @create 2021-04-20 下午3:10
 * @description
 */
@Repository
public interface UserDeviceRepository extends JpaRepository<UserDevice, Long> {
    UserDevice getTopByUserIdOrderByCreateTimeDesc(Long userId);
    List<UserDevice> findByUserIdAndDeviceType(Long userId,int type);

    List<UserDevice> findByUserId(Long userId);
}
