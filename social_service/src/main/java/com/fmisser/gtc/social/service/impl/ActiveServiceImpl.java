package com.fmisser.gtc.social.service.impl;

import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.social.domain.Active;
import com.fmisser.gtc.social.domain.Call;
import com.fmisser.gtc.social.domain.User;
import com.fmisser.gtc.social.repository.ActiveRepository;
import com.fmisser.gtc.social.repository.CallRepository;
import com.fmisser.gtc.social.service.ActiveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;

@Service
public class ActiveServiceImpl implements ActiveService {

    @Autowired
    private ActiveRepository activeRepository;

    @Autowired
    private CallRepository callRepository;

    @Override
    public int inviteCall(User user, Long roomId) throws ApiException {
        Active active = prepareCallActive(user, roomId, 101);
        activeRepository.save(active);
        return 1;
    }

    @Override
    public int cancelCall(User user, Long roomId) throws ApiException {
        Active active = prepareCallActive(user, roomId, 102);
        activeRepository.save(active);
        return 1;
    }

    @Override
    public int timeoutCall(User user, Long roomId) throws ApiException {
        Active active = prepareCallActive(user, roomId, 103);
        activeRepository.save(active);
        return 1;
    }

    @Override
    public int rejectCall(User user, Long roomId) throws ApiException {
        Active active = prepareCallActive(user, roomId, 104);
        activeRepository.save(active);
        return 1;
    }

    @Override
    public int acceptCall(User user, Long roomId) throws ApiException {
        Active active = prepareCallActive(user, roomId, 105);
        activeRepository.save(active);
        return 1;
    }

    @Override
    public int handsCall(User user, Long roomId) throws ApiException {
        Active active = prepareCallActive(user, roomId, 107);
        activeRepository.save(active);
        return 1;
    }

    @Override
    public int endCall(User user, Long roomId) throws ApiException {
        Active active = prepareCallActive(user, roomId, 106);
        activeRepository.save(active);
        return 1;
    }

    @Override
    public boolean isCallBusy(User user) throws ApiException {
        Active active = activeRepository.findTopByUserIdAndType(user.getId(), 1);
        if (Objects.nonNull(active)) {
            if (active.getStatus() == 101 || active.getStatus() == 105) {
                // 这时候再去判断下他联的房间有没有结束
                Call call = callRepository.findByRoomId(active.getRoomId());
                if (Objects.nonNull(call) && call.getIsFinished() == 0) {
                    // 双重保险 房间也没有结束时才算
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public int updateCall(User user, Long roomId) throws ApiException {
        Active active = prepareCallActive(user, roomId, 201);
        activeRepository.save(active);
        return 1;
    }

    @Override
    public boolean isUserCalling(Long userId, Long roomId) throws ApiException {
        Active active = activeRepository
                .findTopByUserIdAndStatusAndRoomIdOrderByActiveTimeDesc(userId, 201, roomId);
        if (Objects.nonNull(active)) {
            //
            Date now = new Date();
            Date lastActiveTime = active.getActiveTime();
            long delta = now.getTime() - lastActiveTime.getTime();
            if (delta / 1000 > 10) {
                // 超过15秒没收到更新数据 则认为不在房间了
                return false;
            }
        }

        return true;
    }

    private Active prepareCallActive(User user, Long roomId, int status) {
        Active active = new Active();
        active.setUserId(user.getId());
        active.setIdentity(user.getIdentity());
        active.setType(1);
        active.setStatus(status);
        active.setRoomId(roomId);
        return active;
    }
}
