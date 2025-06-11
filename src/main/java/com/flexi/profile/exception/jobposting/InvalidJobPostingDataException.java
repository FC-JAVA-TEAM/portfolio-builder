package com.flexi.profile.exception.jobposting;

import org.springframework.http.HttpStatus;

public class InvalidJobPostingDataException extends RuntimeException {
    private final HttpStatus status;

    public InvalidJobPostingDataException(String message) {
        super(message);
        this.status = HttpStatus.BAD_REQUEST;
    }

    public InvalidJobPostingDataException(String field, String reason) {
        super(String.format("Invalid job posting data: %s is invalid. Reason: %s", field, reason));
        this.status = HttpStatus.BAD_REQUEST;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
