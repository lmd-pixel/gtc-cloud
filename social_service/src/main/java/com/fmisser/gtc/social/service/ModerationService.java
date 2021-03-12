package com.fmisser.gtc.social.service;

import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.social.domain.Moderation;

import java.util.List;

public interface ModerationService {
    List<Moderation> getModerationList() throws ApiException;
}
