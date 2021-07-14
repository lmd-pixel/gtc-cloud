package com.fmisser.fpp.oss.cos.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

/**
 * @author by fmisser
 * @create 2021/7/13 5:11 下午
 * @description https://cloud.tencent.com/document/product/436/47316
 */

@Data
@JacksonXmlRootElement(localName = "Request")
public class VideoAuditRequest {
    @JacksonXmlProperty(localName = "Input")
    private Input input;

    @JacksonXmlProperty(localName = "Conf")
    private Conf conf;

    @Data
    public static class Input {
        @JacksonXmlProperty(localName = "Object")
        private String object;
    }

    @Data
    public static class Snapshot {
        @JacksonXmlProperty(localName = "Mode")
        private String mode;

        @JacksonXmlProperty(localName = "TimeInterval")
        private String timeInterval;

        @JacksonXmlProperty(localName = "Count")
        private String count;
    }

    @Data
    public static class Conf {
        @JacksonXmlProperty(localName = "DetectType")
        private String detectType;

        @JacksonXmlProperty(localName = "Snapshot")
        private Snapshot snapshot;

        @JacksonXmlProperty(localName = "Callback")
        private String callback;

        @JacksonXmlProperty(localName = "BizType")
        private String bizType;

        @JacksonXmlProperty(localName = "DetectContent")
        private String detectContent;
    }
}
