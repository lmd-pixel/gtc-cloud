package com.fmisser.gtc.social.repository;

import com.fmisser.gtc.social.domain.Label;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LabelRepository extends JpaRepository<Label, Long> {
    Label findByName(String name);

    @Query(nativeQuery = true, value = "SELECT * FROM t_label WHERE disabled = 0")
    List<Label> getLabelList();
}
