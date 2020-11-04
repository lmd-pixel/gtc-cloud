package com.fmisser.gtc.base.exception;

public enum ApiErrorEnum {
    NO_ERROR(0, ""),

    // http request

    BAD_REQUEST(400, "msg.api.err.bad_request"),

    UNAUTHORIZED(401, "msg.api.err.unauthorized"),

    NOT_FOUND(404, "msg.api.err.not_found"),

    METHOD_NOT_ALLOWED(415, "msg.api.err.method_not_allowed"),

    INTERNAL_SERVER_ERROR(500, "msg.api.err.internal_server_error"),

    // spring secure

    ACCESS_DENIED(1001, "msg.api.err.access_denied"),

    BAD_CREDENTIALS(1002, "msg.api.err.bad_credentials"),

    // domain error

    USER_ALREADY_EXIST(10001, "msg.api.err.user_already_exist"),

    ADD_USER_FAILED(10002, "msg.api.err.add_user_failed"),

    GET_ROLE_FAILED(10003, "msg.api.err.get_role_failed"),

    ADD_USER_ROLE_FAILED(10004, "msg.api.err.add_user_role_failed"),
    ;


    private final int code;
    private final String localeCode;

    private ApiErrorEnum(int code, String localeCode) {
        this.code = code;
        this.localeCode = localeCode;
    }

    public int getCode() {
        return this.code;
    }

    public String getLocaleCode() {
        return localeCode;
    }
}
