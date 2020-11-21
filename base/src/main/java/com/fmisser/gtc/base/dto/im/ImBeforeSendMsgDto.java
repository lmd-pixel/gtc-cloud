package com.fmisser.gtc.base.dto.im;

import lombok.Data;

import java.util.List;

@Data
public class ImBeforeSendMsgDto extends ImBase {
    private String CallbackCommand;
    private String From_Account;
    private String To_Account;
    private Long MsgSeq;
    private Long MsgRandom;
    private Long MsgTime;
    private String MsgKey;
    private List<ImMsgBody> MsgBody;
}
