package com.fmisser.gtc.base.dto.im;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 发送单聊消息的返回结构
 */

@EqualsAndHashCode(callSuper = true)
@Data
public class ImSendMsgCbResp extends ImCbResp {
    @JsonProperty("MsgTime")
    private Long MsgTime;

    @JsonProperty("MsgKey")
    private String MsgKey;
}
