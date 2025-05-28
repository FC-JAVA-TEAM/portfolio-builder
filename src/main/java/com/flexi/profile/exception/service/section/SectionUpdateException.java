package com.flexi.profile.exception.service.section;

import com.flexi.profile.exception.service.common.BusinessLogicException;

public class SectionUpdateException extends BusinessLogicException {
    private static final String DEFAULT_CODE = "SECTION_UPDATE_ERROR";

    public SectionUpdateException(Long sectionId) {
        super(String.format("Failed to update section with id: %d", sectionId), DEFAULT_CODE);
    }

    public SectionUpdateException(Long profileId, Long sectionId) {
        super(String.format("Failed to update section %d in profile %d", sectionId, profileId), DEFAULT_CODE);
    }

    public SectionUpdateException(String message) {
        super(message, DEFAULT_CODE);
    }

    public SectionUpdateException(String message, Throwable cause) {
        super(message, cause, DEFAULT_CODE);
    }

    public SectionUpdateException(String message, String code) {
        super(message, code);
    }

    public SectionUpdateException(String message, String code, Object... args) {
        super(message, code, args);
    }

    public SectionUpdateException(String message, Throwable cause, String code) {
        super(message, cause, code);
    }

    public SectionUpdateException(String message, Throwable cause, String code, Object... args) {
        super(message, cause, code, args);
    }
}
