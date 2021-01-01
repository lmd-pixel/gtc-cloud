package com.fmisser.gtc.base.dto.im;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 回调基本数据
 */

@Data
public class ImBase {
    @JsonProperty("ClientIP")
    private String ClientIP;

    @JsonProperty("OptPlatform")
    private String OptPlatform;
}
