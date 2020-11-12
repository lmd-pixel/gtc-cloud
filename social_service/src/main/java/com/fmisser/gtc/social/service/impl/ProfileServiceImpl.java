package com.fmisser.gtc.social.service.impl;

import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.base.response.ApiResp;
import com.fmisser.gtc.social.domain.Label;
import com.fmisser.gtc.social.repository.LabelRepository;
import com.fmisser.gtc.social.service.ProfileService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProfileServiceImpl implements ProfileService {
    private final LabelRepository labelRepository;

    public ProfileServiceImpl(LabelRepository labelRepository) {
        this.labelRepository = labelRepository;
    }

    @Override
    public ApiResp<List<Label>> labels() throws ApiException {
        return ApiResp.succeed(labelRepository.findAll());
    }

    @Override
    public ApiResp<List<String>> chatPrices(int type) throws ApiException {
        List<String> list = new ArrayList<>();
        BigDecimal startPrice = BigDecimal.valueOf(100);
        BigDecimal endPrice = BigDecimal.valueOf(501);
        BigDecimal stepPrice = BigDecimal.valueOf(50);
        while (startPrice.compareTo(endPrice) < 0) {
            list.add(startPrice.toString());
            startPrice = startPrice.add(stepPrice);
        }
        return ApiResp.succeed(list);
    }
}
