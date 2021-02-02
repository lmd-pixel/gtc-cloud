package com.fmisser.gtc.base.dto.social;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 接收礼物数据
 */

@Data
@NoArgsConstructor
public class RecvGiftDto {

    public RecvGiftDto(Long giftCount, String giftName, String giftImage) {
        this.giftCount = giftCount;
        this.giftName = giftName;
        this.giftImage = giftImage;
    }

    private Long giftCount;
    private String giftName;

    @JsonIgnore
    private String giftImage;

    private String giftImageUrl;
}
