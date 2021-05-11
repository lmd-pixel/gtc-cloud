package com.fmisser.fpp.thirdparty.apple.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * @author fmisser
 * @create 2021-05-11 下午4:29
 * @description iap receipt 验证返回的数据结构，这里只做了部分数据解析
 * 详细参考： https://developer.apple.com/documentation/appstorereceipts/responsebody
 */
@Data
public class IapReceiptVerifyResp {
    private int status;
    private String environment;
    private Receipt receipt;

    @Data
    public static class Receipt {
        private String receipt_type;
        private String bundle_id;
        private List<In_app> in_app;
    }

    @Data
    public static class In_app {
        private int quantity;
        private String product_id;
        private String transaction_id;

        @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private Date purchase_date;
    }
}
