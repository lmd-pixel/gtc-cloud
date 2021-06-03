package com.fmisser.gtc.social.service.impl;

import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.social.domain.GreetMessage;
import com.fmisser.gtc.social.repository.GreetMessageRepository;
import com.fmisser.gtc.social.service.GreetMessageService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class GreetMessageServiceImpl implements GreetMessageService {
    private final GreetMessageRepository greetMessageRepository;

    @Override
    public List<GreetMessage> getRandGreetMessage(int count, int type) throws ApiException {
        return greetMessageRepository.findRandGreetMessageList(count, type);
    }

    @Override
    public List<GreetMessage> getMessageList(List<Integer> idList) throws ApiException {
        return greetMessageRepository.findAllById(idList);
    }

    @Override
    public List<GreetMessage> getRandGreetMessage(int count, int type, String lang) throws ApiException {
        return greetMessageRepository.findRandGreetMessageListWithLang(count, type, lang);
    }
}
