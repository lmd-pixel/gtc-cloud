package com.fmisser.gtc.social.service.impl;

import com.fmisser.gtc.base.dto.social.RecommendAnchorDto;
import com.fmisser.gtc.base.dto.social.RecommendDto;
import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.base.prop.OssConfProp;
import com.fmisser.gtc.social.repository.RecommendRepository;
import com.fmisser.gtc.social.service.RecommendService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

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

    @SneakyThrows
    @Override
    public List<RecommendAnchorDto> getRandRecommendAnchorListForCall(Integer count) throws ApiException {
        List<RecommendAnchorDto> recommendAnchorDtoList =
                recommendRepository.getRandRecommendAnchor(4);

        Date now = new Date();
        recommendAnchorDtoList = recommendAnchorDtoList.stream()
                .filter(dto -> (isTimeBetween(now, dto.getStartTime(), dto.getEndTime()) ||
                        isTimeBetween(now, dto.getStartTime2(), dto.getEndTime2())))
                .collect(Collectors.toList());

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
                String headUrl = String.format("%s/%s/thumbnail_%s",
                        ossConfProp.getMinioVisitUrl(),
                        ossConfProp.getUserProfileBucket(),
                        recommendAnchorDto.getHead());
                recommendAnchorDto.setHeadUrl(headUrl);
            }
        }
        return recommendAnchorDtoList;
    }

    // 判断"HH:mm:ss"时间是否在某个时间段（可能跨天）
    @SneakyThrows
    private static boolean isTimeBetween(Date time, Date startTime, Date endTime) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        Date finalNow = dateFormat.parse(dateFormat.format(time));

        Date dayEnd = dateFormat.parse("23:59:59");
        Date dayStart = dateFormat.parse("00:00:00");

        if (Objects.nonNull(startTime) && Objects.nonNull(endTime)) {
            if (startTime.before(endTime)) {
                // 不超过24点
                if ((finalNow.after(startTime) || finalNow.equals(startTime)) &&
                        finalNow.before(endTime)) {
                    return true;
                }
            } else {
                // 超过24点
                if ((finalNow.after(startTime) || finalNow.equals(startTime) && finalNow.before(dayEnd)) ||
                        (finalNow.after(dayStart) || finalNow.equals(dayStart)) && finalNow.before(endTime)) {
                    return true;
                }
            }
        } else {
            // 时间都是空认为是全天
            return true;
        }

        return false;
    }
}
