package com.flexi.profile.exception.jobposting;

import com.flexi.profile.model.JobPosting.JobStatus;
import org.springframework.http.HttpStatus;

public class JobPostingStatusException extends RuntimeException {
    private final HttpStatus status;

    public JobPostingStatusException(String message) {
        super(message);
        this.status = HttpStatus.CONFLICT;
    }

    public JobPostingStatusException(JobStatus currentStatus, JobStatus targetStatus) {
        super(String.format("Cannot transition job posting from status '%s' to '%s'", currentStatus, targetStatus));
        this.status = HttpStatus.CONFLICT;
    }

    public JobPostingStatusException(JobStatus currentStatus, String operation) {
        super(String.format("Cannot perform operation '%s' on job posting with status '%s'", operation, currentStatus));
        this.status = HttpStatus.CONFLICT;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
