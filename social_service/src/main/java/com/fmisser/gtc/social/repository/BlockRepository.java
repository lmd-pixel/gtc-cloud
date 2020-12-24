package com.fmisser.gtc.social.repository;

import com.fmisser.gtc.social.domain.Block;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;

@Repository
public interface BlockRepository extends JpaRepository<Block, Long> {
    Block findByUserIdAndTypeAndBlockUserId(Long userId, int type, Long blockUserId);
    List<Block> findByUserIdAndBlockUserIdAndTypeIsIn(Long userId, Long blockUserId, List<Integer> types);
}
