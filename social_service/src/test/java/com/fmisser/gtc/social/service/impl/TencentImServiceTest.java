package com.fmisser.gtc.social.service.impl;

import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class TencentImServiceTest {

    @Test
    public void genRoomIdTest() {
        Random random = new Random();

        // 为了以后能扩展， 保留 1Y以上的房间号
        Integer randomInt;
        do {
            randomInt = Math.abs(random.nextInt());
        } while (randomInt >= 100000000);

        System.out.println(randomInt);
    }

}