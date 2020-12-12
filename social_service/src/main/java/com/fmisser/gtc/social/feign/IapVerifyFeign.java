package com.fmisser.gtc.social.feign;

import com.fmisser.gtc.base.dto.social.IapReceiptDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URI;

/**
 * 苹果支付
 */

@FeignClient(url = "", name = "iap-verify")
@Service
public interface IapVerifyFeign {
    @PostMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    IapReceiptDto verifyReceipt(URI uri, @RequestParam("receipt-data") String receiptData);
}
