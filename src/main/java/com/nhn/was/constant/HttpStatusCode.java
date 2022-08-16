package com.nhn.was.constant;

import java.util.Optional;
import java.util.function.Predicate;

public enum HttpStatusCode {
    OK(200, Category.SUCCESSFUL, "OK"),
    BAD_REQUEST(400, Category.CLIENT_ERROR, "Bad Request"),
    UNAUTHORIZED(401, Category.CLIENT_ERROR, "Unauthorized"),
    FORBIDDEN(403, Category.CLIENT_ERROR, "Forbidden"),
    NOT_FOUND(404, Category.CLIENT_ERROR, "Not Found"),
    METHOD_NOT_ALLOWED(405, Category.CLIENT_ERROR, "Method Not Allowed"),
    INTERNAL_SERVER_ERROR(500, Category.SERVER_ERROR, "Internal Server Error");

    private final Integer code;
    private final Category category;
    private final String message;

    private HttpStatusCode(Integer code, Category category, String message) {
        this.code = code;
        this.category = category;
        this.message = message;
    }

    public String getMessage(Throwable e){
        return this.getMessage(this.getMessage() + "\n" + e.getMessage());
    }

    public String getMessage(String message){
        return Optional.ofNullable(message)
                .filter(Predicate.not(String::isBlank))
                .orElse(getMessage());
    }

    @Override
    public String toString(){
        return String.format("%s (%d)", name(), this.getCode());
    }

    public Integer getCode() {
        return this.code;
    }

    public Category getCategory() {
        return this.category;
    }

    public String getMessage() {
        return this.message;
    }

    public enum Category {
        SUCCESSFUL, CLIENT_ERROR, SERVER_ERROR;
    }
}
