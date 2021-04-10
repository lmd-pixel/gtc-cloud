package com.fmisser.fpp.mq.rabbit.service;

public interface MqService {
    String send(String queue, String message) throws RuntimeException;
}
