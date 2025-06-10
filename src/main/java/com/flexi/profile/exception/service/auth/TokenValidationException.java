package com.flexi.profile.exception.service.auth;

import com.flexi.profile.exception.service.common.ServiceException;

public class TokenValidationException extends ServiceException {
    private static final String DEFAULT_CODE = "TOKEN_VALIDATION_ERROR";

    public TokenValidationException(String message) {
        super(message, DEFAULT_CODE);
    }

    public TokenValidationException(String message, Throwable cause) {
        super(message, cause, DEFAULT_CODE);
    }

    public TokenValidationException(String message, String code) {
        super(message, code);
    }

    public TokenValidationException(String message, String code, Object... args) {
        super(message, code, args);
    }

    public TokenValidationException(String message, Throwable cause, String code) {
        super(message, cause, code);
    }

    public TokenValidationException(String message, Throwable cause, String code, Object... args) {
        super(message, cause, code, args);
    }
}
