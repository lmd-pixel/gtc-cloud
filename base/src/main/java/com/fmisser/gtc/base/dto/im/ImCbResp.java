package com.fmisser.gtc.base.dto.im;

import lombok.Data;

/**
 * im 回调后的返回结构
 */

@Data
public class ImCbResp {
    private String ActionStatus = "FAIL";
    private int ErrorCode = 0;
    private String ErrorInfo = "";
}
