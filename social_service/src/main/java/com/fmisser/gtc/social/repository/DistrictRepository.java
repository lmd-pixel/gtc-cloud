package com.fmisser.gtc.social.repository;

import com.fmisser.gtc.social.domain.District;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DistrictRepository extends JpaRepository<District, Integer> {
    @Query(value = "SELECT * FROM t_district WHERE type IN (1, 2)", nativeQuery = true)
    List<District> getDistrictList();
}
