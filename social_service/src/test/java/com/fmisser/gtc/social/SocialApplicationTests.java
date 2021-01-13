package com.fmisser.gtc.social;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

//@SpringBootTest
class SocialApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void floatTest() {
        float a = 0.125f;
        double b = 0.125d;
        System.out.println((a - b) == 0.0);
        System.out.println((b - a) == 0.0);

        double c = 0.8d;
        double d = 0.7d;
        double e = 0.6d;
        System.out.println(c - d);
        System.out.println(d - e);
        System.out.println((c - d) == (d - e));

        System.out.println(1.0 / 0);

        System.out.println(0.0 / 0.0);
    }

}
