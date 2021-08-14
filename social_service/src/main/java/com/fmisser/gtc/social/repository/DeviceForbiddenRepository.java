package com.fmisser.gtc.social.repository;

import com.fmisser.gtc.base.dto.social.DeviceForbiddenDto;
import com.fmisser.gtc.social.domain.DeviceForbidden;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;

/**
 * @author by fmisser
 * @create 2021/6/23 2:25 下午
 * @description TODO
 */

@Repository
public interface DeviceForbiddenRepository extends JpaRepository<DeviceForbidden, Long> {

    DeviceForbidden getDeviceForbiddenByUserIdAndDeviceIdAndDisable(Long userId,long deviceId,int disable);
    DeviceForbidden getDeviceForbiddenByUserIdAndIpAndDisable(Long userId,String ip,int disable);



    @Query(value = "SELECT tf.id AS id, tu.digit_id AS digitId,tu.identity As identity, tu.nick AS nick, " +
            "tf.days AS days, tf.message AS message, tf.start_time AS startTime, tf.end_time AS endTime,tf.ip AS ipAdress,tud.device_android_id AS deviceName " +
            "FROM t_device_forbidden tf " +
            "INNER JOIN t_user tu ON tu.id = tf.user_id AND " +
            "(tu.digit_id LIKE CONCAT('%', ?1, '%') OR ?1 IS NULL) AND " +
            "(tu.nick LIKE CONCAT('%', ?2, '%') OR ?2 IS NULL) AND " +
            "(tu.identity LIKE CONCAT('%', ?3, '%') OR ?3 IS NULL ) " +
            "LEFT JOIN t_user_device tud on tud.id =tf.device_id AND"+
            "(tud.device_android_id LIKE CONCAT('%', ?4, '%') OR ?4 IS NULL ) " +
            "WHERE tf.disable = 0 AND " +
            "(tf.ip LIKE CONCAT('%', ?5, '%') OR ?5 IS NULL ) AND" +
            "(days <= 0 OR (start_time IS NOT NULL AND end_time IS NOT NULL AND ?6 BETWEEN start_time AND end_time))",
            countQuery = "SELECT COUNT(*) FROM t_device_forbidden tf " +
                    "INNER JOIN t_user tu ON tu.id = tf.user_id AND " +
                    "(tu.digit_id LIKE CONCAT('%', ?1, '%') OR ?1 IS NULL) AND " +
                    "(tu.nick LIKE CONCAT('%', ?2, '%') OR ?2 IS NULL) AND " +
                    "(tu.identity LIKE CONCAT('%', ?3, '%') OR ?3 IS NULL ) " +
                    "LEFT JOIN t_user_device tud on tud.id =tf.device_id AND"+
                    "(tud.device_name LIKE CONCAT('%', ?4, '%') OR ?4 IS NULL ) " +
                    "WHERE tf.disable = 0 AND " +
                    "(tf.ip LIKE CONCAT('%', ?5, '%') OR ?5 IS NULL ) AND" +
                    "(days = 0 OR (start_time IS NOT NULL AND end_time IS NOT NULL AND ?6 BETWEEN start_time AND end_time))",
            nativeQuery = true)

    Page<DeviceForbiddenDto> getDeviceForbiddenList(String digitId, String nick, Integer identity, String deviceName,String ipAddress,Date time, Pageable pageabls);

}
