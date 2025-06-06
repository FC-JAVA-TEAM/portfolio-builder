package com.flexi.profile.exception.repository;

import com.flexi.profile.exception.base.BaseException;

public class RepositoryException extends BaseException {
    private static final String DEFAULT_CODE = "REPOSITORY_ERROR";

    public RepositoryException(String message) {
        super(message, DEFAULT_CODE);
    }

    public RepositoryException(String message, Throwable cause) {
        super(message, cause, DEFAULT_CODE);
    }

    public RepositoryException(String message, String code) {
        super(message, code);
    }

    public RepositoryException(String message, String code, Object... args) {
        super(message, code, args);
    }

    public RepositoryException(String message, Throwable cause, String code) {
        super(message, cause, code);
    }

    public RepositoryException(String message, Throwable cause, String code, Object... args) {
        super(message, cause, code, args);
    }
}
