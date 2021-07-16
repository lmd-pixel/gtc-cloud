//package com.fmisser.fpp.oss.cos.dto;
//
//import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
//import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
//import lombok.Data;
//
///**
// * @author by fmisser
// * @create 2021/7/13 5:31 下午
// * @description TODO
// */
//
//@Data
//@JacksonXmlRootElement(localName = "Response")
//public class VideoAuditResponse {
//    @JacksonXmlProperty(localName = "JobsDetail")
//    private JobsDetail jobsDetail;
//
//    @Data
//    public static class JobsDetail {
//        @JacksonXmlProperty(localName = "JobId")
//        private String jobId;
//
//        @JacksonXmlProperty(localName = "State")
//        private String state;
//
//        @JacksonXmlProperty(localName = "CreationTime")
//        private String creationTime;
//    }
//}
