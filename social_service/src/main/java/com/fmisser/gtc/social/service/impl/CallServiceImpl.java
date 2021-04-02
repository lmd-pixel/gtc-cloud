package com.fmisser.gtc.social.service.impl;

import com.fmisser.gtc.base.dto.social.UserCallDto;
import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.base.prop.OssConfProp;
import com.fmisser.gtc.social.domain.User;
import com.fmisser.gtc.social.repository.CallRepository;
import com.fmisser.gtc.social.service.CallService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;

@Service
public class CallServiceImpl implements CallService {

    private final CallRepository callRepository;
    private final OssConfProp ossConfProp;

    public CallServiceImpl(CallRepository callRepository,
                           OssConfProp ossConfProp) {
        this.callRepository = callRepository;
        this.ossConfProp = ossConfProp;
    }

    @Override
    public List<UserCallDto> getCallList(User user, int pageIndex, int pageSize) throws ApiException {
        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        Page<UserCallDto> userCallDtoPage = callRepository.getUserCallList(user.getId(), pageable);
        List<UserCallDto> userCallDtoList = userCallDtoPage.getContent();
        userCallDtoList
                .forEach(userCallDto -> {
                    if (userCallDto.getDuration() > 0) {
                        userCallDto.setConnected(1L);
                    } else {
                        if (Objects.nonNull(userCallDto.getFinishTime())) {
                            long delta = userCallDto.getFinishTime().getTime() -
                                    userCallDto.getCreateTime().getTime();
                            if (delta > 10) {
                                // 未接听
                                userCallDto.setConnected(0L);
                            } else {
                                // 已取消
                                userCallDto.setConnected(-1L);
                            }
                        } else {
                            // 如果没有 finish time 显示未接听
                            userCallDto.setConnected(-1L);
                        }
                    }

                    if (!StringUtils.isEmpty(userCallDto.getHead())) {
                        String headUrl = String.format("%s/%s/%s",
                                ossConfProp.getMinioVisitUrl(),
                                ossConfProp.getUserProfileBucket(),
                                userCallDto.getHead());
                        String headThumbnailUrl = String.format("%s/%s/thumbnail_%s",
                                ossConfProp.getMinioVisitUrl(),
                                ossConfProp.getUserProfileBucket(),
                                userCallDto.getHead());
                        userCallDto.setHeadUrl(headUrl);
                        userCallDto.setHeadThumbnailUrl(headThumbnailUrl);
                    }
                });

        return userCallDtoList;
    }
}
