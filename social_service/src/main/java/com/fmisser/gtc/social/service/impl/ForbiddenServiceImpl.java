package com.fmisser.gtc.social.service.impl;

import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.social.domain.Forbidden;
import com.fmisser.gtc.social.domain.User;
import com.fmisser.gtc.social.repository.ForbiddenRepository;
import com.fmisser.gtc.social.service.ForbiddenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ForbiddenServiceImpl implements ForbiddenService {

    private final ForbiddenRepository forbiddenRepository;

    public ForbiddenServiceImpl(ForbiddenRepository forbiddenRepository) {
        this.forbiddenRepository = forbiddenRepository;
    }

    @Override
    public int forbidden(User user, int days, String message) throws ApiException {
        Forbidden forbidden = getUserForbidden(user);
        if (Objects.nonNull(forbidden)) {
            throw new ApiException(-1, "用户已经被封号！");
        }

        forbidden = new Forbidden();
        forbidden.setUserId(user.getId());
        forbidden.setMessage(message);
        forbidden.setDays(days);

        Date startTime = new Date();
        forbidden.setStartTime(startTime);

        if (days > 0) {

            Date endTime = new Date(startTime.getTime() + days * 3600 * 1000);
            forbidden.setEndTime(endTime);
        }

        forbiddenRepository.save(forbidden);

        return 1;
    }

    @Override
    public int disableForbidden(User user) throws ApiException {
        List<Forbidden> forbiddenList = forbiddenRepository.findByUserIdAndDisable(user.getId(), 0);
        List<Forbidden> newForbiddenList = forbiddenList.stream()
                .peek(forbidden -> forbidden.setDisable(1)).collect(Collectors.toList());
        forbiddenRepository.saveAll(newForbiddenList);

        return 1;
    }

    @Override
    public Forbidden getUserForbidden(User user) throws ApiException {
        Date now = new Date();
        return forbiddenRepository.getCurrentForbidden(user.getId(), now);
    }
}
