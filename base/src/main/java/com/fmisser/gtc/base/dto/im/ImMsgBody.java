package com.fmisser.gtc.base.dto.im;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * im 消息体
 */

@Data
public class ImMsgBody {
    @JsonProperty("MsgType")
    private String MsgType;

    @JsonProperty("MsgContent")
    private ImMsgContent MsgContent;

    @Data
    public static class ImMsgContent {
        @JsonProperty("Text")
        private String Text;

        @JsonProperty("Desc")
        private String Desc;

        @JsonProperty("Data")
        private String Data;
    }

    @Data
    public static class ImVoiceContent {
        @JsonProperty("Url")
        private String Url;

        @JsonProperty("Size")
        private Long Size;

        @JsonProperty("Second")
        private Long Second;

        @JsonProperty("Download_Flag")
        private Long Download_Flag;
    }

    @Data
    public static class ImImageContent {
        @JsonProperty("UUID")
        private String UUID;

        @JsonProperty("ImageFormat")
        private String ImageFormat;

        @JsonProperty("ImageInfoArray")
        List<ImageInfo> ImageInfoArray;
    }

    @Data
    public static class ImageInfo {
        @JsonProperty("Type")
        private Long Type;

        @JsonProperty("Size")
        private Long Size;

        @JsonProperty("Width")
        private Long Width;

        @JsonProperty("Height")
        private Long Height;

        @JsonProperty("URL")
        private String URL;
    }
}
