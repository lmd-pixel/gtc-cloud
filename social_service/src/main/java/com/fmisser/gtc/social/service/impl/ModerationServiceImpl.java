package com.fmisser.gtc.social.service.impl;

import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.social.domain.Moderation;
import com.fmisser.gtc.social.repository.ModerationRepository;
import com.fmisser.gtc.social.service.ModerationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ModerationServiceImpl implements ModerationService {

    private final ModerationRepository moderationRepository;

    public ModerationServiceImpl(ModerationRepository moderationRepository) {
        this.moderationRepository = moderationRepository;
    }

    @Override
    public List<Moderation> getModerationList() throws ApiException {
        return moderationRepository.getModerationList(0);
    }

    @Override
    public List<Moderation> getDynamicModerationList(int type) throws ApiException {
        return moderationRepository.getModerationList(type);
    }
}
