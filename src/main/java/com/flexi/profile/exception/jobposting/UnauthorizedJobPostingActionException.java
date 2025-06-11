package com.flexi.profile.exception.jobposting;

import com.flexi.profile.exception.UnauthorizedException;

public class UnauthorizedJobPostingActionException extends UnauthorizedException {
    public UnauthorizedJobPostingActionException(String message) {
        super(message);
    }

    public UnauthorizedJobPostingActionException(Long userId, Long jobPostingId, String action) {
        super(String.format("User %d is not authorized to %s job posting %d", userId, action, jobPostingId));
    }
}
