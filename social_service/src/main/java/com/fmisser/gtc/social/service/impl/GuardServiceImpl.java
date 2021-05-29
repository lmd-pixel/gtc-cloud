package com.fmisser.gtc.social.service.impl;

import com.fmisser.gtc.base.dto.social.GuardDto;
import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.social.domain.Guard;
import com.fmisser.gtc.social.domain.User;
import com.fmisser.gtc.social.repository.GuardRepository;
import com.fmisser.gtc.social.service.GuardService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author by fmisser
 * @create 2021/5/28 11:10 上午
 * @description TODO
 */

@Slf4j
@Service
@AllArgsConstructor
public class GuardServiceImpl implements GuardService {
    private final GuardRepository guardRepository;

    @Override
    public List<GuardDto> getAnchorGuardList(User user) throws ApiException {
        return guardRepository.getAnchorGuardList(user.getId());
    }

    @Override
    public List<GuardDto> getUserGuardList(User user) throws ApiException {
        return guardRepository.getUserGuardList(user.getId());
    }

    @Override
    public int becomeGuard(User from, User to) throws ApiException {
        if (isGuard(from, to)) {
            log.warn("[guard] userFrom: {} is already userTo: {} guard.", from.getDigitId(), to.getDigitId());
            return 0;
        }

        Guard guard = new Guard();
        guard.setUserIdFrom(from.getId());
        guard.setUserIdTo(to.getId());
        guardRepository.save(guard);

        log.info("[guard] userFrom: {} is become userTo: {} guard.", from.getDigitId(), to.getDigitId());

        return 1;
    }

    @Override
    public boolean isGuard(User from, User to) throws ApiException {
        Optional<Guard> guardOptional = guardRepository
                .findByUserIdFromAndUserIdToAndIsDelete(from.getId(), to.getId(), 0);
        return guardOptional.isPresent();
    }
}
