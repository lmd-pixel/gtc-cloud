package com.fmisser.gtc.social.service.impl;

import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class TencentImServiceTest {

    @Test
    public void genRoomIdTest() {
        Random random = new Random();
        int roomId = Math.abs(random.nextInt());
        System.out.println(roomId);
    }

}