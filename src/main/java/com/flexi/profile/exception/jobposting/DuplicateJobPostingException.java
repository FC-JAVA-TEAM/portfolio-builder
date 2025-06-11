package com.flexi.profile.exception.jobposting;

import org.springframework.http.HttpStatus;

public class DuplicateJobPostingException extends RuntimeException {
    private final HttpStatus status;

    public DuplicateJobPostingException(String message) {
        super(message);
        this.status = HttpStatus.CONFLICT;
    }

    public DuplicateJobPostingException(String title, String department) {
        super(String.format("A job posting with title '%s' already exists in department '%s'", title, department));
        this.status = HttpStatus.CONFLICT;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
