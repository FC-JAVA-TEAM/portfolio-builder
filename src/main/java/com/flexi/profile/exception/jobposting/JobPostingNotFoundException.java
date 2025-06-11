package com.flexi.profile.exception.jobposting;

import com.flexi.profile.exception.ResourceNotFoundException;

public class JobPostingNotFoundException extends ResourceNotFoundException {
    public JobPostingNotFoundException(String message) {
        super(message);
    }

    public JobPostingNotFoundException(Long jobPostingId) {
        super(String.format("Job posting with ID %d not found", jobPostingId));
    }
}
