//package com.fmisser.fpp.oss.cos.feign;
//
//import com.fmisser.fpp.oss.cos.dto.VideoAuditQueryResponse;
//import com.fmisser.fpp.oss.cos.dto.VideoAuditRequest;
//import com.fmisser.fpp.oss.cos.dto.VideoAuditResponse;
//import org.springframework.cloud.openfeign.FeignClient;
//import org.springframework.http.MediaType;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestHeader;
//
//import java.net.URI;
//
///**
// * @author by fmisser
// * @create 2021/7/9 2:41 下午
// * @description TODO
// */
//
//@FeignClient(name = "tencent-cos", url = "https://cloud.tencent.com")
//public interface CosFeign {
//    @GetMapping(value = "")
//    String cosPictureAudit(URI uri, @RequestHeader("Authorization") String cosAuth);
//
//    @PostMapping(value = "", produces = MediaType.APPLICATION_XML_VALUE, consumes = MediaType.APPLICATION_XML_VALUE)
//    VideoAuditResponse cosVideoAudit(URI uri,
//                                     @RequestHeader("Authorization") String cosAuth,
//                                     @RequestBody VideoAuditRequest request);
//
//    @GetMapping(value = "", produces = MediaType.APPLICATION_XML_VALUE, consumes = MediaType.APPLICATION_XML_VALUE)
//    VideoAuditQueryResponse cosVideAuditQuery(URI uri, @RequestHeader("Authorization") String cosAuth);
//}
