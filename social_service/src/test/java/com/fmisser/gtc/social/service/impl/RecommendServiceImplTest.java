package com.fmisser.gtc.social.service.impl;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class RecommendServiceImplTest {

    @SneakyThrows
    @Test
    void testDate() {
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        Date finalNow = dateFormat.parse(dateFormat.format(now));

        Date dayEnd = dateFormat.parse("23:59:59");
        Date dayStart = dateFormat.parse("00:00:00");

        assertTrue(finalNow.before(dayEnd));
        assertTrue(finalNow.after(dayStart));
    }

}