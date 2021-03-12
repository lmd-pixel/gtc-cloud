package com.fmisser.gtc.social.service;

import com.fmisser.gtc.base.exception.ApiException;

public interface SpreadService {
    int addSpread(int type, String idfa) throws ApiException;
}
