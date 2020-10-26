package com.fmisser.gtc.social.service;

import com.fmisser.gtc.social.domain.Young;

public interface YoungService {
    Young create(String phone, String code, int gender);
    Young login(String phone, String code);
}
