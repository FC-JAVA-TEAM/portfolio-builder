package com.flexi.profile.exception.jobapplication;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ApplicationValidationException extends RuntimeException {
    private final String field;
    private final String message;
    private final HttpStatus status;

    public ApplicationValidationException(String field, String message) {
        super(field + ": " + message);
        this.field = field;
        this.message = message;
        this.status = HttpStatus.BAD_REQUEST;
    }

    public String getField() {
        return field;
    }

    public String getMessage() {
        return message;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
