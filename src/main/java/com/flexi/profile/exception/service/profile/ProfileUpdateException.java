package com.flexi.profile.exception.service.profile;

import com.flexi.profile.exception.service.common.BusinessLogicException;

public class ProfileUpdateException extends BusinessLogicException {
    private static final String DEFAULT_CODE = "PROFILE_UPDATE_ERROR";

    public ProfileUpdateException(Long profileId) {
        super(String.format("Failed to update profile with id: %d", profileId), DEFAULT_CODE);
    }

    public ProfileUpdateException(String message) {
        super(message, DEFAULT_CODE);
    }

    public ProfileUpdateException(String message, Throwable cause) {
        super(message, cause, DEFAULT_CODE);
    }

    public ProfileUpdateException(String message, String code) {
        super(message, code);
    }

    public ProfileUpdateException(String message, String code, Object... args) {
        super(message, code, args);
    }

    public ProfileUpdateException(String message, Throwable cause, String code) {
        super(message, cause, code);
    }

    public ProfileUpdateException(String message, Throwable cause, String code, Object... args) {
        super(message, cause, code, args);
    }
}
