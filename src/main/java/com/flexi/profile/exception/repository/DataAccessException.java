package com.flexi.profile.exception.repository;

public class DataAccessException extends RepositoryException {
    private static final String DEFAULT_CODE = "DATA_ACCESS_ERROR";

    public DataAccessException(String message) {
        super(message, DEFAULT_CODE);
    }

    public DataAccessException(String message, Throwable cause) {
        super(message, cause, DEFAULT_CODE);
    }

    public DataAccessException(String message, String code) {
        super(message, code);
    }

    public DataAccessException(String message, String code, Object... args) {
        super(message, code, args);
    }

    public DataAccessException(String message, Throwable cause, String code) {
        super(message, cause, code);
    }

    public DataAccessException(String message, Throwable cause, String code, Object... args) {
        super(message, cause, code, args);
    }
}
