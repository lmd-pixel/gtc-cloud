package com.fmisser.gtc.base.dto.im;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ImOfflinePushInfo {
    @JsonProperty("PushFlag")
    private int PushFlag;

    @JsonProperty("Title")
    private String Title;

    @JsonProperty("Desc")
    private String Desc;

    @JsonProperty("Ext")
    private String Ext;

    @JsonProperty("AndroidInfo")
    private ImAndroidInfo AndroidInfo;

    @JsonProperty("ApnsInfo")
    private ImApnsInfo ApnsInfo;

    @Data
    public static class ImAndroidInfo {
        @JsonProperty("Sound")
        private String Sound;
    }

    @Data
    public static class ImApnsInfo {
        @JsonProperty("Sound")
        private String Sound;

        @JsonProperty("BadgeMode")
        private int BadgeMode;

        @JsonProperty("Title")
        private String Title;

        @JsonProperty("SubTitle")
        private String SubTitle;

        @JsonProperty("Image")
        private String Image;
    }
}


