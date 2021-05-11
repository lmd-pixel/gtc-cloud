package com.fmisser.fpp.thirdparty.apple.feign;

import com.fmisser.fpp.thirdparty.apple.dto.IapReceiptVerifyReq;
import com.fmisser.fpp.thirdparty.apple.dto.IapReceiptVerifyResp;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.net.URI;

/**
 * @author fmisser
 * @create 2021-05-11 下午5:00
 * @description iap 校验票据
 */

@FeignClient(url = "https://www.apple.com", name = "iap-receipt-verify")
public interface IapReceiptVerifyFeign {

    /**
     * 验证票据
     */
    @PostMapping(value = "")
    IapReceiptVerifyResp receiptVerify(URI uri, @RequestBody IapReceiptVerifyReq req);
}
