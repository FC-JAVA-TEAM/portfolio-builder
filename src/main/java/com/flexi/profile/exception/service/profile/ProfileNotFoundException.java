package com.flexi.profile.exception.service.profile;

import com.flexi.profile.exception.service.common.ResourceNotFoundException;

public class ProfileNotFoundException extends ResourceNotFoundException {
    private static final String DEFAULT_CODE = "PROFILE_NOT_FOUND";

    public ProfileNotFoundException(Long profileId) {
        super("Profile", "id", profileId);
    }

    public ProfileNotFoundException(String userId) {
        super(String.format("Profile not found for user with id: %s", userId), DEFAULT_CODE);
    }

    public ProfileNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProfileNotFoundException(String message, String code) {
        super(message, code);
    }

    public ProfileNotFoundException(String message, String code, Object... args) {
        super(message, code, args);
    }

    public ProfileNotFoundException(String message, Throwable cause, String code) {
        super(message, cause, code);
    }

    public ProfileNotFoundException(String message, Throwable cause, String code, Object... args) {
        super(message, cause, code, args);
    }
}
