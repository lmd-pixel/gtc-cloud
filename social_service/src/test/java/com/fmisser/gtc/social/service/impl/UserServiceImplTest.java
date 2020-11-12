package com.fmisser.gtc.social.service.impl;

import com.fmisser.gtc.base.utils.DateUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Date;

class UserServiceImplTest {

    @Test
    void calcDigitId() {
        String digitId = UserServiceImpl.calcDigitId(LocalDateTime.now(), 999);
        System.out.println(digitId);
        Assertions.assertEquals(8, digitId.length());
    }


    @Test
    void getHourStart() {
        LocalDateTime thisHourStart = DateUtils.getHourStart(LocalDateTime.now());
        System.out.println(thisHourStart);
    }

    @Test
    void getHourEnd() {
        LocalDateTime thisHourEnd = DateUtils.getHourEnd(LocalDateTime.now());
        System.out.println(thisHourEnd);
    }

    @Test
    void getAgeFromBirth() {
        Date birth = new Date(86, 5, 26);
        System.out.println(DateUtils.getAgeFromBirth(birth));
    }

    @Test
    void getConstellationFromBirth() {
        Date birth = new Date(86, 5, 26);
        System.out.println(DateUtils.getConstellationFromBirth(birth));
    }
}