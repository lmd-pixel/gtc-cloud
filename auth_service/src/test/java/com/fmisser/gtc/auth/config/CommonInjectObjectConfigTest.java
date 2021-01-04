package com.fmisser.gtc.auth.config;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

class CommonInjectObjectConfigTest {

    @Test
    void passwordEncoder() {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String testSecret = passwordEncoder.encode("test-client-secret");
        String prodSecret = passwordEncoder.encode("comm-client-secret");

//        System.out.println(testSecret);
        System.out.println(prodSecret);
    }
}