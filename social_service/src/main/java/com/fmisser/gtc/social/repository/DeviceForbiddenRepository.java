package com.fmisser.gtc.social.repository;

import com.fmisser.gtc.social.domain.DeviceForbidden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author by fmisser
 * @create 2021/6/23 2:25 下午
 * @description TODO
 */

@Repository
public interface DeviceForbiddenRepository extends JpaRepository<DeviceForbidden, Long> {

}
