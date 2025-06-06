package com.flexi.profile.exception.repository;

public class EntityNotFoundException extends RepositoryException {
    private static final String DEFAULT_CODE = "ENTITY_NOT_FOUND";

    public EntityNotFoundException(String entityName, Object id) {
        super(String.format("%s with id %s not found", entityName, id), DEFAULT_CODE);
    }

    public EntityNotFoundException(String message) {
        super(message, DEFAULT_CODE);
    }

    public EntityNotFoundException(String message, Throwable cause) {
        super(message, cause, DEFAULT_CODE);
    }

    public EntityNotFoundException(String message, String code) {
        super(message, code);
    }

    public EntityNotFoundException(String message, String code, Object... args) {
        super(message, code, args);
    }

    public EntityNotFoundException(String message, Throwable cause, String code) {
        super(message, cause, code);
    }

    public EntityNotFoundException(String message, Throwable cause, String code, Object... args) {
        super(message, cause, code, args);
    }
}
