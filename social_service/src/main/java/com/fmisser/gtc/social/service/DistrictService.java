package com.fmisser.gtc.social.service;

import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.social.domain.District;

import java.util.List;

public interface DistrictService {
    List<District> getDistrictList() throws ApiException;
}
