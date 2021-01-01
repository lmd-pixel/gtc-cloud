package com.fmisser.gtc.base.dto.im;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户状态改变
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ImStateChangeDto extends ImBase {
    @JsonProperty("CallbackCommand")
    private String CallbackCommand;

    @JsonProperty("Info")
    private StateChangeInfo Info;

    @Data
    public static class StateChangeInfo {
        @JsonProperty("Action")
        private String Action;

        @JsonProperty("To_Account")
        private String To_Account;

        @JsonProperty("Reason")
        private String Reason;
    }
}


