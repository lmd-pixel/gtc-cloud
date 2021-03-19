package com.fmisser.gtc.base.dto.wx;

import java.util.Arrays;

public class WebHookFactory {
    static public WebHookDto buildTextMsg(String message) {
        WebHookDto webHookDto = new WebHookDto();
        webHookDto.setMsgtype("text");

        WebHookDto.TextMsg textMsg = new WebHookDto.TextMsg();
        textMsg.setContent(message);
        textMsg.setMentioned_list(Arrays.asList("@all"));

        webHookDto.setText(textMsg);

        return webHookDto;
    }
}
