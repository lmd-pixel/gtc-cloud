package com.fmisser.gtc.social.service;

import com.fmisser.gtc.base.dto.im.ImAfterSendMsgDto;
import com.fmisser.gtc.base.dto.im.ImBeforeSendMsgDto;
import com.fmisser.gtc.base.dto.im.ImCbResp;
import com.fmisser.gtc.base.dto.im.ImStateChangeDto;
import com.fmisser.gtc.base.exception.ApiException;

/**
 * IM 回调服务
 */

public interface ImCallbackService {
    // 状态改变
    Object stateChangeCallback(ImStateChangeDto imStateChangeDto);

    Object beforeSendMsg(ImBeforeSendMsgDto imBeforeSendMsgDto, String originContent);

    Object afterSendMsg(ImAfterSendMsgDto imAfterSendMsgDto, String originContent);

    int textModeration(String userId, String text, String bizType, int moderationType);

    boolean textNoPrice(String text);

}
