package com.fmisser.gtc.social.service;

import com.fmisser.gtc.base.dto.social.GuardDto;
import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.social.domain.User;

import java.util.List;

/**
 * @author by fmisser
 * @create 2021/5/28 11:10 上午
 * @description TODO
 */
public interface GuardService {
    List<GuardDto> getAnchorGuardList(User user) throws ApiException;
    List<GuardDto> getUserGuardList(User user) throws ApiException;
    int becomeGuard(User from, User to) throws ApiException;
    boolean isGuard(User from, User to) throws ApiException;
}
