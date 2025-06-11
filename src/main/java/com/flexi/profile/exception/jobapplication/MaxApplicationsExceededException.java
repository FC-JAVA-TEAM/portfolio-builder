package com.flexi.profile.exception.jobapplication;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
public class MaxApplicationsExceededException extends RuntimeException {
    private final Long userId;
    private final int maxLimit;
    private final HttpStatus status;

    public MaxApplicationsExceededException(Long userId, int maxLimit) {
        super("User " + userId + " has exceeded the maximum limit of " + maxLimit + " applications");
        this.userId = userId;
        this.maxLimit = maxLimit;
        this.status = HttpStatus.TOO_MANY_REQUESTS;
    }

    public Long getUserId() {
        return userId;
    }

    public int getMaxLimit() {
        return maxLimit;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
