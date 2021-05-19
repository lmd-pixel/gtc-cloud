package com.fmisser.gtc.social.service.impl;

import com.fmisser.gtc.base.dto.social.ForbiddenDto;
import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.social.domain.Forbidden;
import com.fmisser.gtc.social.domain.User;
import com.fmisser.gtc.social.repository.ForbiddenRepository;
import com.fmisser.gtc.social.service.ForbiddenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.*;
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
            throw new ApiException(-1, "用户已经被封号或账号已注销！");
        }

        forbidden = new Forbidden();
        forbidden.setUserId(user.getId());
        forbidden.setMessage(message);
        forbidden.setDays(days);

        Date startTime = new Date();
        forbidden.setStartTime(startTime);

        if (days > 0) {

            Date endTime = new Date(startTime.getTime() + (long) days * 3600 * 1000 * 24);
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
        return forbiddenRepository.getCurrentForbiddenV2(user.getId(), new Date());
    }

    @Override
    public Pair<List<ForbiddenDto>, Map<String, Object>> getForbiddenList(String digitId, String nick, Integer identity,
                                                         Integer pageSize, Integer pageIndex) throws ApiException {
        Pageable pageable = PageRequest.of(pageIndex - 1, pageSize);
        Page<ForbiddenDto> forbiddenDtoPage = forbiddenRepository
                .getForbiddenListV2(digitId, nick, identity, new Date(), pageable);

        Map<String, Object> extra = new HashMap<>();
        extra.put("totalPage", forbiddenDtoPage.getTotalPages() );
        extra.put("totalEle", forbiddenDtoPage.getTotalElements());
        extra.put("currPage", pageIndex);

        return Pair.of(forbiddenDtoPage.getContent(), extra);
    }

    @Override
    public int disableForbidden(Long forbiddenId) throws ApiException {
        Optional<Forbidden> optionalForbidden = forbiddenRepository.findById(forbiddenId);
        if (!optionalForbidden.isPresent()) {
            throw new ApiException(-1, "封号不存在!");
        }

        Forbidden forbidden = optionalForbidden.get();
        forbidden.setDisable(1);
        forbiddenRepository.save(forbidden);

        return 1;
    }
}
