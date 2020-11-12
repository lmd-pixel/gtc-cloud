package com.fmisser.gtc.social.repository;

import com.fmisser.gtc.social.domain.Label;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LabelRepository extends JpaRepository<Label, Long> {
    Label findByName(String name);
}
