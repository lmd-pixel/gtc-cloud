package com.fmisser.gtc.social.service;

import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.social.domain.Label;

import java.util.List;

public interface LabelService {
    List<Label> getLabelList() throws ApiException;
}
