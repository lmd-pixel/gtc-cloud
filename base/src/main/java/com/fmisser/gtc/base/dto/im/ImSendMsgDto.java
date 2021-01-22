package com.fmisser.gtc.base.dto.im;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * 发送单聊消息体结构 https://cloud.tencent.com/document/product/269/2282
 */
@Data
public class ImSendMsgDto {
    // 注意大写开头的属性，由于生成的get set 符合驼峰标准，所有大写开头和小写开头的属性生成的get set函数一样，
    // 当json 解析的时候会认为首字母是小写，造成生成属性名称错误，如果你需要开头大写的属性
    // 所以通过 JsonProperty 注解避免该问题
    @JsonProperty("SyncOtherMachine")
    private int SyncOtherMachine;

    @JsonProperty("From_Account")
    private String From_Account;

    @JsonProperty("To_Account")
    private String To_Account;

    @JsonProperty("MsgLifeTime")
    private int MsgLifeTime;

    @JsonProperty("MsgRandom")
    private int MsgRandom;

    @JsonProperty("MsgTimeStamp")
    private int MsgTimeStamp;

    @JsonProperty("ForbidCallbackControl")
    private List<String> ForbidCallbackControl;

    @JsonProperty("MsgBody")
    private List<ImMsgBody> MsgBody;

    @JsonProperty("OfflinePushInfo")
    private ImOfflinePushInfo OfflinePushInfo;
}
