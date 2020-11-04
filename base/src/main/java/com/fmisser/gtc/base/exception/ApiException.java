package com.fmisser.gtc.base.exception;

public class ApiException extends RuntimeException {
    private int code;

    public ApiException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
