package com.fmisser.gtc.social.service.impl;

import com.fmisser.gtc.base.dto.social.UserCallDto;
import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.social.domain.Call;
import com.fmisser.gtc.social.domain.User;
import com.fmisser.gtc.social.repository.CallRepository;
import com.fmisser.gtc.social.service.CallService;
import com.fmisser.gtc.social.service.CommonService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class CallServiceImpl implements CallService {
    private final CallRepository callRepository;
    private final CommonService commonService;

    @Override
    public List<UserCallDto> getCallList(User user, int pageIndex, int pageSize) throws ApiException {
        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        Date now = new Date();
        Date threeDaysBefore = new Date(now.getTime() - 3 * 24 * 3600 * 1000);
        Page<UserCallDto> userCallDtoPage = callRepository.getUserCallList(user.getId(), threeDaysBefore, pageable);
        List<UserCallDto> userCallDtoList = userCallDtoPage.getContent();
        userCallDtoList
                .forEach(userCallDto -> {
                    if (userCallDto.getDuration() > 0) {
                        userCallDto.setConnected(1L);
                    } else {
                        if (Objects.nonNull(userCallDto.getFinishTime())) {
                            long delta = userCallDto.getFinishTime().getTime() -
                                    userCallDto.getCreateTime().getTime();
                            if (delta > 10 * 1000) {
                                // 未接通
                                userCallDto.setConnected(0L);
                            } else {
                                // 已取消
                                userCallDto.setConnected(-1L);
                            }
                        } else {
                            // 如果没有 finish time 显示未接通
                            userCallDto.setConnected(-1L);
                        }
                    }

                    commonService.getUserProfileHeadCompleteUrl(userCallDto.getHead())
                            .ifPresent(v -> {
                                userCallDto.setHeadUrl(v.getFirst());
                                userCallDto.setHeadThumbnailUrl(v.getSecond());
                            });
                });

        return userCallDtoList;
    }

    @Override
    public Call getCallByRoomId(Long roomId) throws ApiException {
        return callRepository.findByRoomId(roomId);
    }
}
