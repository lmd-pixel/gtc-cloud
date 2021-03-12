package com.fmisser.gtc.social.service.impl;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WithdrawServiceImplTest {

    @Test
    void testCreateOrderNumber() {
        for (int i = 0; i < 10; i++) {
            String orderNumber = WithdrawServiceImpl.createWithdrawOrderNumber(10L);
            System.out.println(orderNumber);
        }
    }

}