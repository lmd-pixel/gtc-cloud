package com.fmisser.gtc.base.dto.im;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * @author by fmisser
 * @create 2021/5/20 2:51 下午
 * @description 设置用户资料返回结果
 */

@EqualsAndHashCode(callSuper = true)
@Data
public class ImProfileSetResp extends ImCbResp {
    @JsonProperty("ErrorDisplay")
    private String ErrorDisplay = "";
}
