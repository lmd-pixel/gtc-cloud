package com.fmisser.gtc.social.service;

import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.social.domain.GreetMessage;

import java.util.List;

public interface GreetMessageService {
    List<GreetMessage> getRandGreetMessage(int count) throws ApiException;
    List<GreetMessage> getMessageList(List<Integer> idList) throws ApiException;
}
