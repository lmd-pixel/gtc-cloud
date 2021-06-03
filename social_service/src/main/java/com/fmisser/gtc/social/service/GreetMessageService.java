package com.fmisser.gtc.social.service;

import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.social.domain.GreetMessage;

import java.util.List;

public interface GreetMessageService {
    List<GreetMessage> getRandGreetMessage(int count, int type) throws ApiException;
    List<GreetMessage> getMessageList(List<Integer> idList) throws ApiException;
    List<GreetMessage> getRandGreetMessage(int count, int type, String lang) throws ApiException;
}
