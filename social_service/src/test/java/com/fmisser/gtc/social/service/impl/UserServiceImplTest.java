package com.fmisser.gtc.social.service.impl;

import com.fmisser.gtc.base.utils.ArrayUtils;
import com.fmisser.gtc.base.utils.DateUtils;
import com.fmisser.gtc.social.domain.UserMaterial;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    @Test
    void testDeepClone() {

        List<UserMaterial> userMaterialList = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            UserMaterial userMaterial = new UserMaterial();
            userMaterial.setUserId((long) i);
            userMaterialList.add(userMaterial);
        }

        List<UserMaterial> deepCloneList = ArrayUtils.deepCopy(userMaterialList);

        userMaterialList.get(0).setUserId(999L);
        Assertions.assertNotEquals(999L, deepCloneList.get(0).getUserId());
    }
}