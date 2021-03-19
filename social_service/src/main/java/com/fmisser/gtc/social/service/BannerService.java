package com.fmisser.gtc.social.service;

import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.social.domain.Banner;

import java.util.List;

public interface BannerService {
    List<Banner> getBannerList(String lang) throws ApiException;
    List<Banner> getAuditBannerList(String lang) throws ApiException;
}
