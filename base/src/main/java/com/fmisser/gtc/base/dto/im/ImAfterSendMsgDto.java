package com.fmisser.gtc.base.dto.im;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 发送消息以后的回调
 */

@EqualsAndHashCode(callSuper = true)
@Data
public class ImAfterSendMsgDto extends ImBase {
    @JsonProperty("CallbackCommand")
    private String CallbackCommand;

    @JsonProperty("From_Account")
    private String From_Account;

    @JsonProperty("To_Account")
    private String To_Account;

    @JsonProperty("MsgSeq")
    private Long MsgSeq;

    @JsonProperty("MsgRandom")
    private Long MsgRandom;

    @JsonProperty("MsgTime")
    private Long MsgTime;

    @JsonProperty("MsgKey")
    private String MsgKey;

    @JsonProperty("SendMsgResult")
    private int SendMsgResult;

    @JsonProperty("ErrorInfo")
    private String ErrorInfo;

    @JsonProperty("MsgBody")
    private List<ImMsgBody> MsgBody;
}
