package com.flexi.profile.exception.service.section;

import com.flexi.profile.exception.service.common.ResourceNotFoundException;

public class SectionNotFoundException extends ResourceNotFoundException {
    private static final String DEFAULT_CODE = "SECTION_NOT_FOUND";

    public SectionNotFoundException(Long sectionId) {
        super("Section", "id", sectionId);
    }

    public SectionNotFoundException(Long profileId, Long sectionId) {
        super(String.format("Section with id %d not found in profile %d", sectionId, profileId), DEFAULT_CODE);
    }

    public SectionNotFoundException(String message) {
        super(message, DEFAULT_CODE);
    }

    public SectionNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public SectionNotFoundException(String message, String code) {
        super(message, code);
    }

    public SectionNotFoundException(String message, String code, Object... args) {
        super(message, code, args);
    }

    public SectionNotFoundException(String message, Throwable cause, String code) {
        super(message, cause, code);
    }

    public SectionNotFoundException(String message, Throwable cause, String code, Object... args) {
        super(message, cause, code, args);
    }
}
