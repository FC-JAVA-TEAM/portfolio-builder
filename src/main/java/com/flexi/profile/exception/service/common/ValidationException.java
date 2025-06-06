package com.flexi.profile.exception.service.common;

import lombok.Getter;
import java.util.Map;

@Getter
public class ValidationException extends ServiceException {
    private static final String DEFAULT_CODE = "VALIDATION_ERROR";
    private final Map<String, String> errors;

    public ValidationException(String message) {
        super(message, DEFAULT_CODE);
        this.errors = null;
    }

    public ValidationException(String message, Map<String, String> errors) {
        super(message, DEFAULT_CODE);
        this.errors = errors;
    }

    public ValidationException(String message, Map<String, String> errors, Throwable cause) {
        super(message, cause, DEFAULT_CODE);
        this.errors = errors;
    }

    public ValidationException(String message, String code, Map<String, String> errors) {
        super(message, code);
        this.errors = errors;
    }

    public ValidationException(String message, String code, Map<String, String> errors, Throwable cause) {
        super(message, cause, code);
        this.errors = errors;
    }
}
