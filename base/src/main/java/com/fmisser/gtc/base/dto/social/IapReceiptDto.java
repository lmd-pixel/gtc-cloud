package com.fmisser.gtc.base.dto.social;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * iap支付结构, 原始的数据很多，这里不一一解析，只拿部分对比
 */

@Data
public class IapReceiptDto {
    private int status;
    private String environment;
    private Receipt receipt;

    @Data
    public static class Receipt {
        private String receipt_type;
        private String bundle_id;
        private List<InApp> in_app;
    }

    @Data
    public static class InApp {
        private int quantity;
        private String product_id;
        private String transaction_id;

        @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private Date purchase_date;
    }
}
