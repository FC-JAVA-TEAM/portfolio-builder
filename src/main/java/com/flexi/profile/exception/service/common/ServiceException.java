package com.flexi.profile.exception.service.common;

import com.flexi.profile.exception.base.BaseException;

public class ServiceException extends BaseException {
    private static final String DEFAULT_CODE = "SERVICE_ERROR";

    public ServiceException(String message) {
        super(message, DEFAULT_CODE);
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause, DEFAULT_CODE);
    }

    public ServiceException(String message, String code) {
        super(message, code);
    }

    public ServiceException(String message, String code, Object... args) {
        super(message, code, args);
    }

    public ServiceException(String message, Throwable cause, String code) {
        super(message, cause, code);
    }

    public ServiceException(String message, Throwable cause, String code, Object... args) {
        super(message, cause, code, args);
    }
}
