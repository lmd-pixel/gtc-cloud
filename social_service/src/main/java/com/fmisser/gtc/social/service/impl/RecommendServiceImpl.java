package com.fmisser.gtc.social.service.impl;

import com.fmisser.gtc.base.dto.social.RecommendAnchorDto;
import com.fmisser.gtc.base.dto.social.RecommendDto;
import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.base.prop.OssConfProp;
import com.fmisser.gtc.social.repository.RecommendRepository;
import com.fmisser.gtc.social.service.RecommendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class RecommendServiceImpl implements RecommendService {

    private final RecommendRepository recommendRepository;
    private final OssConfProp ossConfProp;

    public RecommendServiceImpl(RecommendRepository recommendRepository,
                                OssConfProp ossConfProp) {
        this.recommendRepository = recommendRepository;
        this.ossConfProp = ossConfProp;
    }

    @Override
    public List<RecommendAnchorDto> getRandRecommendAnchorList(Integer count, int gender) throws ApiException {
        List<RecommendAnchorDto> recommendAnchorDtoList =
                recommendRepository.getRandRecommendAnchorWithGender(3, gender);
        List<RecommendAnchorDto> randList = new ArrayList<>();

        count = Math.min(count, recommendAnchorDtoList.size());
        for (int i = 0; i < count; i++) {
            randList.add(recommendAnchorDtoList.remove(new Random().nextInt(recommendAnchorDtoList.size())));
        }

        return _prepareRecommendDtoResponse(randList);
    }

    private List<RecommendAnchorDto> _prepareRecommendDtoResponse(List<RecommendAnchorDto> recommendAnchorDtoList) throws ApiException {
        for (RecommendAnchorDto recommendAnchorDto: recommendAnchorDtoList) {
            if (!StringUtils.isEmpty(recommendAnchorDto.getHead())) {
                String headUrl = String.format("%s/%s/%s",
                        ossConfProp.getMinioVisitUrl(),
                        ossConfProp.getUserProfileBucket(),
                        recommendAnchorDto.getHead());
                recommendAnchorDto.setHeadUrl(headUrl);
            }
        }
        return recommendAnchorDtoList;
    }
}
