package com.flexi.profile.exception.repository;

public class DuplicateKeyException extends RepositoryException {
    private static final String DEFAULT_CODE = "DUPLICATE_KEY";

    public DuplicateKeyException(String entityName, String fieldName, Object fieldValue) {
        super(String.format("A %s with %s '%s' already exists", entityName, fieldName, fieldValue), DEFAULT_CODE);
    }

    public DuplicateKeyException(String message) {
        super(message, DEFAULT_CODE);
    }

    public DuplicateKeyException(String message, Throwable cause) {
        super(message, cause, DEFAULT_CODE);
    }

    public DuplicateKeyException(String message, String code) {
        super(message, code);
    }

    public DuplicateKeyException(String message, String code, Object... args) {
        super(message, code, args);
    }

    public DuplicateKeyException(String message, Throwable cause, String code) {
        super(message, cause, code);
    }

    public DuplicateKeyException(String message, Throwable cause, String code, Object... args) {
        super(message, cause, code, args);
    }
}
