package com.fmisser.gtc.base.dto.im;

import lombok.Data;

@Data
public class ImMsgBody {
    private String MsgType;
    private ImMsgContent MsgContent;

    @Data
    public static class ImMsgContent {
        private String Text;
        private String Desc;
        private String Data;
    }
}
