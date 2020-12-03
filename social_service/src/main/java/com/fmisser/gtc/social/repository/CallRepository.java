package com.fmisser.gtc.social.repository;

import com.fmisser.gtc.base.dto.social.AnchorCallBillDto;
import com.fmisser.gtc.social.domain.Call;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface CallRepository extends JpaRepository<Call, Long> {
    // 查询语音或者视频会话数
    @Query(value = "SELECT COUNT(id) FROM t_call " +
            "WHERE type = ?1 AND " +
            "is_finished = 1 AND " +
            "(created_time BETWEEN ?2 AND ?3 OR ?2 IS NULL OR ?3 IS NULL)", nativeQuery = true)
    Long countCall(int type, Date startTime, Date endTime);

    // 一次查询语音和视频会话数
    // row1是语音， row2是视频
    @Query(value = "SELECT COUNT(id) FROM t_call " +
            "WHERE is_finished = 1 AND " +
            "(created_time BETWEEN ?1 AND ?2 OR ?2 IS NULL OR ?3 IS NULL) " +
            "GROUP BY type", nativeQuery = true)
    List<Long> countCallOnce(Date startTime, Date endTime);
}
