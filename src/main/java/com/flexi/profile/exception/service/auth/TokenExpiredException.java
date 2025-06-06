package com.flexi.profile.exception.service.auth;

import com.flexi.profile.exception.service.common.ServiceException;

public class TokenExpiredException extends ServiceException {
    private static final String DEFAULT_CODE = "TOKEN_EXPIRED";

    public TokenExpiredException(String message) {
        super(message, DEFAULT_CODE);
    }

    public TokenExpiredException(String message, Throwable cause) {
        super(message, cause, DEFAULT_CODE);
    }

    public TokenExpiredException(String message, String code) {
        super(message, code);
    }

    public TokenExpiredException(String message, String code, Object... args) {
        super(message, code, args);
    }

    public TokenExpiredException(String message, Throwable cause, String code) {
        super(message, cause, code);
    }

    public TokenExpiredException(String message, Throwable cause, String code, Object... args) {
        super(message, cause, code, args);
    }
}
