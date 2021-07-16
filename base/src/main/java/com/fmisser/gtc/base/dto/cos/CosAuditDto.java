package com.fmisser.gtc.base.dto.cos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author by fmisser
 * @create 2021/7/15 3:47 下午
 * @description https://cloud.tencent.com/document/product/436/47297
 */

@Data
public class CosAuditDto {
    private Integer code;

    private String message;

    private CosAuditData data;

    @Data
    public static class CosAuditHeader {
        @JsonProperty("x-cos-meta-dynamic-id")
        private String dynamicId;
    }

    @Data
    public static class CosAuditData {
        @JsonProperty("forbidden_status")
        private Integer forbiddenStatus;

        private Integer result;

        @JsonProperty("trace_id")
        private String traceId;

        private String url;

        @JsonProperty("porn_info")
        private CosAuditInfo pornInfo;

        @JsonProperty("terrorist_info")
        private CosAuditInfo terroristInfo;

        @JsonProperty("politics_info")
        private CosAuditInfo politicsInfo;

        @JsonProperty("ads_info")
        private CosAuditInfo adsInfo;

        @JsonProperty("cos_headers")
        private CosAuditHeader header;
    }

    @Data
    public static class CosAuditInfo {
        @JsonProperty("hit_flag")
        private Integer hitFlag;

        private Integer count;

        private String label;

        private Integer score;
    }
}
