package com.flexi.profile.exception.service.auth;

import com.flexi.profile.exception.service.common.ServiceException;

public class InvalidCredentialsException extends ServiceException {
    private static final String DEFAULT_CODE = "INVALID_CREDENTIALS";

    public InvalidCredentialsException(String message) {
        super(message, DEFAULT_CODE);
    }

    public InvalidCredentialsException(String message, Throwable cause) {
        super(message, cause, DEFAULT_CODE);
    }

    public InvalidCredentialsException(String message, String code) {
        super(message, code);
    }

    public InvalidCredentialsException(String message, String code, Object... args) {
        super(message, code, args);
    }

    public InvalidCredentialsException(String message, Throwable cause, String code) {
        super(message, cause, code);
    }

    public InvalidCredentialsException(String message, Throwable cause, String code, Object... args) {
        super(message, cause, code, args);
    }
}
