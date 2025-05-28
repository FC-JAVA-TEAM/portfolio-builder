package com.flexi.profile.exception.service.auth;

import com.flexi.profile.exception.service.common.ServiceException;

public class RefreshTokenException extends ServiceException {
    private static final String DEFAULT_CODE = "REFRESH_TOKEN_ERROR";

    public RefreshTokenException(String message) {
        super(message, DEFAULT_CODE);
    }

    public RefreshTokenException(String message, Throwable cause) {
        super(message, cause, DEFAULT_CODE);
    }

    public RefreshTokenException(String message, String code) {
        super(message, code);
    }

    public RefreshTokenException(String message, String code, Object... args) {
        super(message, code, args);
    }

    public RefreshTokenException(String message, Throwable cause, String code) {
        super(message, cause, code);
    }

    public RefreshTokenException(String message, Throwable cause, String code, Object... args) {
        super(message, cause, code, args);
    }
}
