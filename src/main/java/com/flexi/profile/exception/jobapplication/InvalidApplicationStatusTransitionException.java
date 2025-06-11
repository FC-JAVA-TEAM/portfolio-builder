package com.flexi.profile.exception.jobapplication;

import com.flexi.profile.model.JobApplication.ApplicationStatus;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidApplicationStatusTransitionException extends RuntimeException {
    private final ApplicationStatus currentStatus;
    private final ApplicationStatus newStatus;
    private final HttpStatus status;

    public InvalidApplicationStatusTransitionException(ApplicationStatus currentStatus, ApplicationStatus newStatus) {
        super("Invalid status transition from " + currentStatus + " to " + newStatus);
        this.currentStatus = currentStatus;
        this.newStatus = newStatus;
        this.status = HttpStatus.BAD_REQUEST;
    }

    public ApplicationStatus getCurrentStatus() {
        return currentStatus;
    }

    public ApplicationStatus getNewStatus() {
        return newStatus;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
