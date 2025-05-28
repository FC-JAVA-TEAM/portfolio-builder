package com.flexi.profile.exception.service.common;

public class BusinessLogicException extends ServiceException {
    private static final String DEFAULT_CODE = "BUSINESS_LOGIC_ERROR";

    public BusinessLogicException(String message) {
        super(message, DEFAULT_CODE);
    }

    public BusinessLogicException(String message, Throwable cause) {
        super(message, cause, DEFAULT_CODE);
    }

    public BusinessLogicException(String message, String code) {
        super(message, code);
    }

    public BusinessLogicException(String message, String code, Object... args) {
        super(message, code, args);
    }

    public BusinessLogicException(String message, Throwable cause, String code) {
        super(message, cause, code);
    }

    public BusinessLogicException(String message, Throwable cause, String code, Object... args) {
        super(message, cause, code, args);
    }
}
