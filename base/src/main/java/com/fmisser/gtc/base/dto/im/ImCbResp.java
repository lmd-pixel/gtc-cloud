package com.fmisser.gtc.base.dto.im;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * im 请求后的返回结构
 */

@Data
public class ImCbResp {
    @JsonProperty("ActionStatus")
    private String ActionStatus = "FAIL";

    @JsonProperty("ErrorCode")
    private int ErrorCode = -1;

    @JsonProperty("ErrorInfo")
    private String ErrorInfo = "";
}
