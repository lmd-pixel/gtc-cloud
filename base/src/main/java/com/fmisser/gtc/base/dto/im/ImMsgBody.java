package com.fmisser.gtc.base.dto.im;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * im 消息体
 */

@Data
public class ImMsgBody {
    @JsonProperty("MsgType")
    private String MsgType;

    @JsonProperty("MsgContent")
    private ImMsgContent MsgContent;

    @Data
    public static class ImMsgContent {
        @JsonProperty("Text")
        private String Text;

        @JsonProperty("Desc")
        private String Desc;

        @JsonProperty("Data")
        private String Data;
    }
}
