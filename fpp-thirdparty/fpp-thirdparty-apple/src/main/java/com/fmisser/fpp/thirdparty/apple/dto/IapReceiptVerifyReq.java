package com.fmisser.fpp.thirdparty.apple.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author fmisser
 * @create 2021-05-11 下午4:16
 * @description 票据验证请求
 */

@Data
@AllArgsConstructor
public class IapReceiptVerifyReq {
    @JsonProperty("receipt-data")
    String receiptData;
}
