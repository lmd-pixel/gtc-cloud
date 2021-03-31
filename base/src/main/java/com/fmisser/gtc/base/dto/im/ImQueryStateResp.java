package com.fmisser.gtc.base.dto.im;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class ImQueryStateResp extends ImCbResp {

    @JsonProperty("QueryResult")
    private List<QueryResult> QueryResult;

    @JsonProperty("ErrorList")
    private List<QueryResult> ErrorList;

    @Data
    public static class QueryResult {
        @JsonProperty("To_Account")
        private String To_Account;

        @JsonProperty("Status")
        private String Status;
    }
}
