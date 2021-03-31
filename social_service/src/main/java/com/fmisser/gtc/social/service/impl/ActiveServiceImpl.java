package com.fmisser.gtc.social.service.impl;

import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.social.domain.User;
import com.fmisser.gtc.social.service.ActiveService;
import org.springframework.stereotype.Service;

@Service
public class ActiveServiceImpl implements ActiveService {
    @Override
    public int inviteCall(User user, Long roomId) throws ApiException {
        return 0;
    }

    @Override
    public int cancelCall(User user, Long roomId) throws ApiException {
        return 0;
    }

    @Override
    public int timeoutCall(User user, Long roomId) throws ApiException {
        return 0;
    }

    @Override
    public int rejectCall(User user, Long roomId) throws ApiException {
        return 0;
    }

    @Override
    public int acceptCall(User user, Long roomId) throws ApiException {
        return 0;
    }

    @Override
    public int endCall(User user, Long roomId) throws ApiException {
        return 0;
    }

    @Override
    public boolean isCallBusy(User user) throws ApiException {
        return false;
    }
}
