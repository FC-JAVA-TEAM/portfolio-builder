package com.flexi.profile.exception.jobposting;

import com.flexi.profile.exception.ResourceNotFoundException;

public class UserNotFoundException extends ResourceNotFoundException {
    public UserNotFoundException(String message) {
        super(message);
    }

    public UserNotFoundException(Long userId) {
        super(String.format("User with ID %d not found", userId));
    }
}
