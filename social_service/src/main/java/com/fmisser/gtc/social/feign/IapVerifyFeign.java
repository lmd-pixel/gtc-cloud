package com.fmisser.gtc.social.feign;

import com.fmisser.gtc.base.dto.apple.IapReceiptVerifyDto;
import com.fmisser.gtc.base.dto.social.IapReceiptDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;

/**
 * 苹果支付
 */

@FeignClient(url = "https://www.apple.com", name = "iap-verify")
@Service
public interface IapVerifyFeign {
//    @PostMapping(value = "")
//    IapReceiptDto verifyReceipt(URI uri, @RequestParam("receipt-data") String receiptData);

    @PostMapping(value = "")
    IapReceiptDto verifyReceipt(URI uri, @RequestBody IapReceiptVerifyDto iapReceiptVerifyDto);

//    @PostMapping(value = "", produces = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
//    IapReceiptDto verifyReceipt(URI uri, Map<String, Object> objectMap);
}
