package com.flexi.profile.exception.service.section;

import com.flexi.profile.exception.service.common.BusinessLogicException;

public class SectionCreationException extends BusinessLogicException {
    private static final String DEFAULT_CODE = "SECTION_CREATION_ERROR";

    public SectionCreationException(Long profileId) {
        super(String.format("Failed to create section for profile with id: %d", profileId), DEFAULT_CODE);
    }

    public SectionCreationException(String message) {
        super(message, DEFAULT_CODE);
    }

    public SectionCreationException(String message, Throwable cause) {
        super(message, cause, DEFAULT_CODE);
    }

    public SectionCreationException(String message, String code) {
        super(message, code);
    }

    public SectionCreationException(String message, String code, Object... args) {
        super(message, code, args);
    }

    public SectionCreationException(String message, Throwable cause, String code) {
        super(message, cause, code);
    }

    public SectionCreationException(String message, Throwable cause, String code, Object... args) {
        super(message, cause, code, args);
    }
}
