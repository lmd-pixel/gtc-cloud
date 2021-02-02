package com.fmisser.gtc.social.service.impl;

import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.social.domain.GreetMessage;
import com.fmisser.gtc.social.repository.GreetMessageRepository;
import com.fmisser.gtc.social.service.GreetMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GreetMessageServiceImpl implements GreetMessageService {

    @Autowired
    private GreetMessageRepository greetMessageRepository;

    @Override
    public List<GreetMessage> getRandGreetMessage(int count) throws ApiException {
        return greetMessageRepository.findRandGreetMessageList(count);
    }

    @Override
    public List<GreetMessage> getMessageList(List<Integer> idList) throws ApiException {
        return greetMessageRepository.findAllById(idList);
    }
}
