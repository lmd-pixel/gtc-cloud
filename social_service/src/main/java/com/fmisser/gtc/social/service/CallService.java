package com.fmisser.gtc.social.service;

import com.fmisser.gtc.base.dto.social.UserCallDto;
import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.social.domain.User;

import java.util.List;

public interface CallService {
    List<UserCallDto> getCallList(User user, int pageIndex, int pageSize) throws ApiException;
}
