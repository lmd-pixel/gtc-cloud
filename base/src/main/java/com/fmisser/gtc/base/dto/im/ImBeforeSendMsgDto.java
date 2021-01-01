package com.fmisser.gtc.base.dto.im;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;



@EqualsAndHashCode(callSuper = true)
@Data
public class ImBeforeSendMsgDto extends ImBase {

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

    @JsonProperty("MsgBody")
    private List<ImMsgBody> MsgBody;
}
