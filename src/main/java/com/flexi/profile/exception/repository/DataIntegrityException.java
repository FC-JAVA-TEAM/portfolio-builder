package com.flexi.profile.exception.repository;

public class DataIntegrityException extends RepositoryException {
    private static final String DEFAULT_CODE = "DATA_INTEGRITY_ERROR";

    public DataIntegrityException(String message) {
        super(message, DEFAULT_CODE);
    }

    public DataIntegrityException(String message, Throwable cause) {
        super(message, cause, DEFAULT_CODE);
    }

    public DataIntegrityException(String message, String code) {
        super(message, code);
    }

    public DataIntegrityException(String message, String code, Object... args) {
        super(message, code, args);
    }

    public DataIntegrityException(String message, Throwable cause, String code) {
        super(message, cause, code);
    }

    public DataIntegrityException(String message, Throwable cause, String code, Object... args) {
        super(message, cause, code, args);
    }
}
