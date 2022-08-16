package com.nhn.was.response;

public class ErrorResponse {
    private final Boolean success;
    private final Integer errorCode;
    private final String message;

    protected ErrorResponse(Boolean success, Integer errorCode, String message) {
        this.success = success;
        this.errorCode = errorCode;
        this.message = message;
    }

    public static ErrorResponse of(Boolean success, Integer errorCode, String message){
        return new ErrorResponse(success, errorCode, message);
    }

    public Boolean getSuccess() {
        return this.success;
    }

    public Integer getErrorCode() {
        return this.errorCode;
    }

    public String getMessage() {
        return this.message;
    }
}
