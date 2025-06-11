package com.flexi.profile.exception.jobapplication;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateApplicationException extends RuntimeException {
    public DuplicateApplicationException(Long userId, Long jobId) {
        super("User with id " + userId + " has already applied for job with id " + jobId);
    }
}
