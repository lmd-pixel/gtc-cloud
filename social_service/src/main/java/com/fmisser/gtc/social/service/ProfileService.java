package com.fmisser.gtc.social.service;

import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.base.response.ApiResp;
import com.fmisser.gtc.social.domain.Label;

import java.util.List;

public interface ProfileService {
    ApiResp<List<Label>> labels() throws ApiException;
    ApiResp<List<String>> chatPrices(int type) throws ApiException;
}
