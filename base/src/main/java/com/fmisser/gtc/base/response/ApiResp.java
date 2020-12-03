package com.fmisser.gtc.base.response;

import com.fmisser.gtc.base.exception.ApiErrorEnum;
import com.fmisser.gtc.base.exception.ApiException;

import java.util.Map;

public class ApiResp<T> {
    private boolean success;
    private long code;
    private String message;
    private T data;
    private Map<String, Object> extra;

    private ApiResp() {

    }

    public boolean isSuccess() {
        return success;
    }

    public long getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

    public Map<String, Object> getExtra() {
        return extra;
    }

    public static <T> ApiResp<T> succeed(T data, String message) {
        ApiResp<T> resp = new ApiResp<T>();
        resp.success = true;
        resp.code = ApiErrorEnum.NO_ERROR.getCode();
        resp.message = message;
        resp.data = data;
        return resp;
    }

    public static <T> ApiResp<T> succeed(T data) {
        ApiResp<T> resp = new ApiResp<T>();
        resp.success = true;
        resp.code = ApiErrorEnum.NO_ERROR.getCode();
        resp.data = data;
        return resp;
    }

    public static <T> ApiResp<T> succeed(T data, Map<String, Object> extra) {
        ApiResp<T> resp = new ApiResp<T>();
        resp.success = true;
        resp.code = ApiErrorEnum.NO_ERROR.getCode();
        resp.data = data;
        resp.extra = extra;
        return resp;
    }

    public static <T> ApiResp<T> failed(int code, String message) {
        ApiResp<T> resp = new ApiResp<T>();
        resp.success = false;
        resp.code = code;
        resp.message = message;
        resp.data = null;
        return resp;
    }

    public static <T> ApiResp<T> failed(ApiException e) {
        return failed(e.getCode(), e.getMessage());
    }
}
