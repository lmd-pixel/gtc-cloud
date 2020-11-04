package com.fmisser.gtc.base.response;

import com.fmisser.gtc.base.exception.ApiErrorEnum;
import com.fmisser.gtc.base.exception.ApiException;

public class ApiResp<T> {
    private boolean success;
    private long code;
    private String message;
    private T data;

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
