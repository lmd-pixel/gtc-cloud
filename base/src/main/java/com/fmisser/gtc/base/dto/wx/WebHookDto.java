package com.fmisser.gtc.base.dto.wx;

import lombok.Data;

import java.util.List;

@Data
public class WebHookDto {

    @Data
    public static class TextMsg {
        private String content;
        List<String> mentioned_list;
        List<String> mentioned_mobile_list;
    }

    private String msgtype;
    private TextMsg text;
}
