package com.flexi.profile.exception.jobapplication;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class UnauthorizedApplicationAccessException extends RuntimeException {
    private final Long userId;
    private final Long applicationId;
    private final String operation;
    private final HttpStatus status;

    public UnauthorizedApplicationAccessException(Long userId, Long applicationId, String operation) {
        super("User " + userId + " is not authorized to " + operation + " application " + applicationId);
        this.userId = userId;
        this.applicationId = applicationId;
        this.operation = operation;
        this.status = HttpStatus.FORBIDDEN;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public String getOperation() {
        return operation;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
