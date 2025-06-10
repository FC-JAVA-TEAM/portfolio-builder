package com.flexi.profile.exception.service.profile;

import com.flexi.profile.exception.service.common.BusinessLogicException;

public class ProfileCreationException extends BusinessLogicException {
    private static final String DEFAULT_CODE = "PROFILE_CREATION_ERROR";

    public ProfileCreationException(String userId) {
        super(String.format("Failed to create profile for user with id: %s", userId), DEFAULT_CODE);
    }

    public ProfileCreationException(String message, Throwable cause) {
        super(message, cause, DEFAULT_CODE);
    }

    public ProfileCreationException(String message, String code) {
        super(message, code);
    }

    public ProfileCreationException(String message, String code, Object... args) {
        super(message, code, args);
    }

    public ProfileCreationException(String message, Throwable cause, String code) {
        super(message, cause, code);
    }

    public ProfileCreationException(String message, Throwable cause, String code, Object... args) {
        super(message, cause, code, args);
    }
}
