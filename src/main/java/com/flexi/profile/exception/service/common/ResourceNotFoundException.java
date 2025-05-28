package com.flexi.profile.exception.service.common;

public class ResourceNotFoundException extends ServiceException {
    private static final String DEFAULT_CODE = "RESOURCE_NOT_FOUND";

    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s not found with %s : '%s'", resourceName, fieldName, fieldValue), DEFAULT_CODE);
    }

    public ResourceNotFoundException(String message) {
        super(message, DEFAULT_CODE);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause, DEFAULT_CODE);
    }

    public ResourceNotFoundException(String message, String code) {
        super(message, code);
    }

    public ResourceNotFoundException(String message, String code, Object... args) {
        super(message, code, args);
    }

    public ResourceNotFoundException(String message, Throwable cause, String code) {
        super(message, cause, code);
    }

    public ResourceNotFoundException(String message, Throwable cause, String code, Object... args) {
        super(message, cause, code, args);
    }
}
