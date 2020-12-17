package com.fmisser.gtc.base.dto.apple;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class IapReceiptVerifyDto {
    @JsonProperty("receipt-data")
    String receiptData;
}
