package com.fmisser.gtc.base.dto.im;

import lombok.Data;

/**
 * 用户状态改变
 */
@Data
public class ImStateChangeDto extends ImBase {
    private String CallbackCommand;
    private StateChangeInfo Info;

    @Data
    public static class StateChangeInfo {
        private String Action;
        private String To_Account;
        private String Reason;
    }
}


