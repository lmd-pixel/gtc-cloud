package com.fmisser.gtc.base.dto.im;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author by fmisser
 * @create 2021/6/2 10:24 上午
 * @description TODO
 */

@EqualsAndHashCode(callSuper = true)
@Data
public class ImTransferMsgCb extends ImCbResp {
    @JsonProperty("MsgBody")
    private List<ImMsgBody> MsgBody;
}
