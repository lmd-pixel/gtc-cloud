package com.fmisser.gtc.base.dto.im;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;

/**
 * @author by fmisser
 * @create 2021/5/20 2:53 下午
 * @description 设置用户资料请求
 */

@Data
public class ImProfileSetReq {
    @JsonProperty("From_Account")
    private String fromAccount;

    @JsonProperty("ProfileItem")
    private List<ProfileItem> profileItems;

    @Data
    @AllArgsConstructor
    public static class ProfileItem {
        @JsonProperty("Tag")
        private String tag;

        @JsonProperty("Value")
        private String value;
    }
}
