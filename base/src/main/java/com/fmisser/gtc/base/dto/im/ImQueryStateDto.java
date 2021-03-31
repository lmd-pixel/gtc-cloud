package com.fmisser.gtc.base.dto.im;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * https://cloud.tencent.com/document/product/269/2566
 */

@Data
public class ImQueryStateDto {
    @JsonProperty("To_Account")
    private List<String> To_Account;
}
