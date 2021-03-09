package com.fmisser.gtc.social.repository;

import com.fmisser.gtc.base.dto.social.CallDetailDto;
import com.fmisser.gtc.base.dto.social.CallDto;
import com.fmisser.gtc.base.dto.social.calc.CalcAnchorProfitDto;
import com.fmisser.gtc.base.dto.social.calc.CalcTotalCallDto;
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

    Call findByRoomId(Long roomId);

    // 查询语音或者视频会话数
    @Query(value = "SELECT COUNT(tc.id) FROM t_call tc " +
            "WHERE tc.type = ?1 AND " +
            "tc.is_finished = 1 AND " +
            "(tc.created_time BETWEEN ?2 AND ?3 OR ?2 IS NULL OR ?3 IS NULL)", nativeQuery = true)
    Long countCall(int type, Date startTime, Date endTime);

    // 一次查询语音和视频会话数
    // row1是语音， row2是视频
    @Query(value = "SELECT COUNT(id) FROM t_call tc " +
            "WHERE tc.is_finished = 1 AND " +
            "(tc.created_time BETWEEN ?1 AND ?2 OR ?2 IS NULL OR ?3 IS NULL) " +
            "GROUP BY type", nativeQuery = true)
    List<Long> countCallOnce(Date startTime, Date endTime);

    // 通话统计
    @Query(value = "SELECT COUNT(DISTINCT callDigitId) AS callUsers, " +
            "COUNT(*) AS callTimes, " +
            "COUNT(DISTINCT CASE WHEN connected=1 THEN acceptDigitId END) AS acceptUsers, " +
            "COUNT(IF(connected=1,TRUE,NULL)) AS acceptTimes, " +
            "SUM(duration) AS duration, " +
            "SUM(card) AS videoCard " +
            "FROM ( SELECT " +
            "IF(tc.call_mode=1,tu2.digit_id,tu.digit_id) AS callDigitId, " +
            "IF(tc.call_mode=1,tu2.nick,tu.nick) AS callNick, " +
            "IF(tc.call_mode=1,tu.digit_id,tu2.digit_id) AS acceptDigitId, " +
            "IF(tc.call_mode=1,tu.nick,tu2.nick) AS acceptNick, " +
            "tc.type AS type, tc.call_mode AS mode, IF(tc.duration>0,1,0) AS connected, " +
            "tc.duration AS duration, tc.created_time AS startTime, tc.finish_time AS finishTime, " +
            "COUNT(IF(tcb.source>0,TRUE,NULL)) AS card " +
            "FROM t_call tc " +
            "INNER JOIN t_user tu ON tc.user_id_from = tu.id " +
            "INNER JOIN t_user tu2 ON tc.user_id_to = tu2.id " +
            "LEFT JOIN t_call_bill tcb ON tcb.call_id = tc.id " +
            "GROUP BY tc.id" +
            ") tcc WHERE " +
            "(callDigitId LIKE CONCAT('%', ?1, '%') OR ?1 IS NULL) AND " +
            "(callNick LIKE CONCAT('%', ?2, '%') OR ?2 IS NULL) AND " +
            "(acceptDigitId LIKE CONCAT('%', ?3, '%') OR ?3 IS NULL) AND " +
            "(acceptNick LIKE CONCAT('%', ?4, '%') OR ?4 IS NULL) AND " +
            "(type LIKE CONCAT('%', ?5, '%') OR ?5 IS NULL) AND " +
            "(connected LIKE CONCAT('%', ?6, '%') OR ?6 IS NULL) AND " +
            "(startTime BETWEEN ?7 AND ?8 OR ?7 IS NULL OR ?8 IS NULL) ",
    nativeQuery = true)
    CalcTotalCallDto calcTotalCall(String callDigitId, String callNick,
                                   String acceptDigitId, String acceptNick,
                                   Integer type, Integer durationType,
                                   Date startTime, Date endTime);

    // 通话列表
    @Query(value = "SELECT * FROM ( " +
            "SELECT tc.id AS callId, " +
            "IF(tc.call_mode=1,tu2.digit_id,tu.digit_id) AS callDigitId, " +
            "IF(tc.call_mode=1,tu2.nick,tu.nick) AS callNick, " +
            "IF(tc.call_mode=1,tu.digit_id,tu2.digit_id) AS acceptDigitId, " +
            "IF(tc.call_mode=1,tu.nick,tu2.nick) AS acceptNick, " +
            "tc.type AS type, tc.call_mode AS mode, IF(tc.duration>0,1,0) AS connected, " +
            "tc.duration AS duration, tc.created_time AS startTime, tc.finish_time AS endTime, " +
            "COUNT(IF(tcb.source>0,TRUE,NULL)) AS freeCard " +
            "FROM t_call tc " +
            "INNER JOIN t_user tu ON tc.user_id_from = tu.id " +
            "INNER JOIN t_user tu2 ON tc.user_id_to = tu2.id " +
            "LEFT JOIN t_call_bill tcb ON tcb.call_id = tc.id " +
            "GROUP BY tc.id" +
            ") tcc " +
            "WHERE " +
            "(callDigitId LIKE CONCAT('%', ?1, '%') OR ?1 IS NULL) AND " +
            "(callNick LIKE CONCAT('%', ?2, '%') OR ?2 IS NULL) AND " +
            "(acceptDigitId LIKE CONCAT('%', ?3, '%') OR ?3 IS NULL) AND " +
            "(acceptNick LIKE CONCAT('%', ?4, '%') OR ?4 IS NULL) AND " +
            "(type LIKE CONCAT('%', ?5, '%') OR ?5 IS NULL) AND " +
            "(connected LIKE CONCAT('%', ?6, '%') OR ?6 IS NULL) AND " +
            "(startTime BETWEEN ?7 AND ?8 OR ?7 IS NULL OR ?8 IS NULL) " +
            "ORDER BY callId DESC LIMIT ?9 OFFSET ?10",
    nativeQuery = true)
    List<CallDto> getCallList(String callDigitId, String callNick,
                              String acceptDigitId, String acceptNick,
                              Integer type, Integer durationType,
                              Date startTime, Date endTime,
                              int limit, int offset);


    // 通话列表详情
    @Query(value = "SELECT " +
            "tc.id AS callId, " +
            "IF(tc.call_mode=1,tu2.digit_id,tu.digit_id) AS callDigitId, " +
            "IF(tc.call_mode=1,tu2.nick,tu.nick) AS callNick, " +
            "IF(tc.call_mode=1,tu.digit_id,tu2.digit_id) AS acceptDigitId, " +
            "IF(tc.call_mode=1,tu.nick,tu2.nick) AS acceptNick, " +
            "IF(tc.duration>0,1,0) AS connected, " +
            "tc.duration AS duration, " +
            "tc.type AS type, " +
            "tc.call_mode AS mode, " +
            "tc.created_time AS startTime, " +
            "tc.finish_time AS endTime, " +
            "COUNT(IF(tcb.source>0,TRUE,NULL)) AS freeCard, " +
            "SUM(tcb.origin_coin) AS consume, " +
            "SUM(tcb.profit_coin) AS profit, " +
            "tu2.video_price as videoPrice, " +
            "tu2.call_price as audioPrice, " +
            "ta.video_profit_ratio as videoRatio, " +
            "ta.voice_profit_ratio as audioRatio " +
            "FROM t_call tc " +
            "LEFT JOIN t_call_bill tcb ON tcb.call_id = tc.id AND tcb.valid = 1 " +
            "INNER JOIN t_user tu ON tu.id = tc.user_id_from " +
            "INNER JOIN t_user tu2 ON tu2.id = tc.user_id_to " +
            "INNER JOIN t_asset ta ON ta.user_id = tc.user_id_to " +
            "WHERE tc.id = ?1 " +
            "GROUP BY tc.id",
    nativeQuery = true)
    CallDetailDto getCallDetail(Long callId);
}
