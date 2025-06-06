package com.flexi.profile.exception.service.profile;

import com.flexi.profile.exception.service.common.BusinessLogicException;

public class ProfileAccessDeniedException extends BusinessLogicException {
    private static final String DEFAULT_CODE = "PROFILE_ACCESS_DENIED";

    public ProfileAccessDeniedException(Long profileId, String userId) {
        super(String.format("Access denied for user %s to profile with id: %d", userId, profileId), DEFAULT_CODE);
    }

    public ProfileAccessDeniedException(String message) {
        super(message, DEFAULT_CODE);
    }

    public ProfileAccessDeniedException(String message, Throwable cause) {
        super(message, cause, DEFAULT_CODE);
    }

    public ProfileAccessDeniedException(String message, String code) {
        super(message, code);
    }

    public ProfileAccessDeniedException(String message, String code, Object... args) {
        super(message, code, args);
    }

    public ProfileAccessDeniedException(String message, Throwable cause, String code) {
        super(message, cause, code);
    }

    public ProfileAccessDeniedException(String message, Throwable cause, String code, Object... args) {
        super(message, cause, code, args);
    }
}
