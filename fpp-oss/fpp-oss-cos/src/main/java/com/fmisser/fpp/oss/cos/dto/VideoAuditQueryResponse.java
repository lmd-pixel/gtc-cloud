package com.fmisser.fpp.oss.cos.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author by fmisser
 * @create 2021/7/13 5:41 下午
 * @description https://cloud.tencent.com/document/product/436/47317
 */

@Data
@JacksonXmlRootElement(localName = "Response")
public class VideoAuditQueryResponse {

    @JacksonXmlProperty(localName = "JobsDetail")
    private JobDetail jobsDetail;

    @JacksonXmlProperty(localName = "NonExistJobIds")
    private String nonExistJobIds;

    @Data
    public static class AudioResult {
        @JacksonXmlProperty(localName = "PornInfo")
        private ResultInfo pornInfo;

        @JacksonXmlProperty(localName = "TerrorismInfo")
        private ResultInfo terrorismInfo;

        @JacksonXmlProperty(localName = "PoliticsInfo")
        private ResultInfo politicsInfo;

        @JacksonXmlProperty(localName = "AdsInfo")
        private ResultInfo adsInfo;
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class Snapshot extends AudioResult {
        @JacksonXmlProperty(localName = "Url")
        private String url;
    }

    @Data
    public static class JobDetail {
        @JacksonXmlProperty(localName = "Code")
        private String code;

        @JacksonXmlProperty(localName = "Message")
        private String message;

        @JacksonXmlProperty(localName = "JobId")
        private String jobId;

        @JacksonXmlProperty(localName = "State")
        private String state;

        @JacksonXmlProperty(localName = "CreationTime")
        private String creationTime;

        @JacksonXmlProperty(localName = "Object")
        private String object;

        @JacksonXmlProperty(localName = "SnapshotCount")
        private String snapshotCount;

        @JacksonXmlProperty(localName = "Result")
        private String result;

        @JacksonXmlProperty(localName = "PornInfo")
        private ResultInfo pornInfo;

        @JacksonXmlProperty(localName = "TerrorismInfo")
        private ResultInfo terrorismInfo;

        @JacksonXmlProperty(localName = "PoliticsInfo")
        private ResultInfo politicsInfo;

        @JacksonXmlProperty(localName = "AdsInfo")
        private ResultInfo adsInfo;

        @JacksonXmlProperty(localName = "Snapshot")
        private Snapshot snapshot;

        @JacksonXmlProperty(localName = "AudioResult")
        private AudioResult audioResult;
    }
}
