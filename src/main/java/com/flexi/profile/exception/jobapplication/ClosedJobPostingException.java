package com.flexi.profile.exception.jobapplication;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ClosedJobPostingException extends RuntimeException {
    private final Long jobPostingId;
    private final HttpStatus status;

    public ClosedJobPostingException(Long jobPostingId) {
        super("Cannot apply to closed job posting with id: " + jobPostingId);
        this.jobPostingId = jobPostingId;
        this.status = HttpStatus.BAD_REQUEST;
    }

    public Long getJobPostingId() {
        return jobPostingId;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
