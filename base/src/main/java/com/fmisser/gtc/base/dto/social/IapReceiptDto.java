package com.fmisser.gtc.base.dto.social;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * iap支付结构
 */

@Data
public class IapReceiptDto {
    private int status;
    private IapReceiptData receipt;

    @Data
    public static class IapReceiptData {
        private int quantity;
        private String product_id;
        private String transaction_id;

        @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private Date purchase_date;

        private String original_transaction_id;

        @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private Date original_purchase_date;

        private String app_item_id;
        private String version_external_identifier;
        private String bid;
        private String bvrs;
    }
}
