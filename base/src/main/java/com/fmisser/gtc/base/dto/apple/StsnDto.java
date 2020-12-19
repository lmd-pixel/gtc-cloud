package com.fmisser.gtc.base.dto.apple;

/**
 * https://developer.apple.com/documentation/appstoreservernotifications/responsebody
 */
public class StsnDto {
    private String auto_renew_adam_id;
    private String auto_renew_product_id;
    private String auto_renew_status;
    private String auto_renew_status_change_date;
    private String auto_renew_status_change_date_ms;
    private String auto_renew_status_change_date_pst;
    private String environment;
    private int expiration_intent;
    private String latest_expired_receipt;
    private String latest_expired_receipt_info;
    private String latest_receipt;
}
