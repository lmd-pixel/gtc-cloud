package com.fmisser.gtc.social.service;

import com.fmisser.gtc.base.dto.social.RecommendAnchorDto;
import com.fmisser.gtc.base.dto.social.RecommendDto;
import com.fmisser.gtc.base.exception.ApiException;

import java.util.List;

public interface RecommendService {
    List<RecommendAnchorDto> getRandRecommendAnchorList(Integer count) throws ApiException;
}
